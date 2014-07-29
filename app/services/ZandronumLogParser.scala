package services

import scala.io._
import scala.util.matching._
import scala.collection.immutable._
import scala.collection.mutable.{ Map => MutableMap }
import java.io.InputStream

class ZandronumLogParser {
  type PlayerStats = Map[String, PlayerData]
  
  private val playerNameRegex = new Regex("""\[.+\] (.+) has connected.""", "name")
  private val playerRenameRegex = new Regex("""\[.+?\] (.+) is now known as (.+)""", "oldName", "newName")
  private val playerJoinedTeamRegex = new Regex("""\[.+?\] (.+) joined the (Red|Blue) team.""", "player", "team")
  private val suicideRegex = new Regex("""\[.+?\] (.+?) (killed himself|killed itself|killed herself|died|melted|should have stood back|can't swim|mutated|was squished|fell too far|suicides|went boom|tried to leave|stood in the wrong spot)\.""", "name")
  private val fragRegex = new Regex(
    """\[\d{2}:\d{2}:\d{2}\]\s(.+?)\s(chewed on|was .+ by|rode|couldn't hide from)\s(.+?)('s\s)?(fist|chainsaw|pea shooter|boomstick|super shotgun|chaingun|rocket|plasma gun|BFG)?\.$""", 
    "killed", "", "killer", "gun")

  def parseLog(source: Source) : PlayerStats = {
    val lines = source.getLines.toArray
    val players = populatePlayers(lines)

    setTeams(getTeams(lines), players)
    setFragCounts(getFragCounts(lines), players)
    setSuicides(getSuicides(lines), players)

    players
  }

  private def populatePlayers(lines: Array[String]) : PlayerStats =
    lines.flatMap(playerNameRegex.findFirstMatchIn(_))
      .map(regex => (regex.group("name"), PlayerData.empty))
      .toMap ++ 
    lines.flatMap(playerRenameRegex.findFirstMatchIn(_))
      .map(regex => (regex.group("newName"), PlayerData.empty))
      .toMap

  private def getFragCounts(lines: Array[String]) = lines
    .flatMap(fragRegex.findFirstMatchIn(_))
    .map(regex => FragData(regex.group("killer"), regex.group("killed")))
    .to[collection.immutable.Seq]

  private def setFragCounts(frags: Seq[FragData], players: PlayerStats) : Unit = 
    frags.foreach { frag => 
      players(frag.killer).frags += 1
      players(frag.killed).deaths += 1
    }

  private def getTeams(lines: Array[String]) : Seq[JoinedTeamData] = lines
    .flatMap(playerJoinedTeamRegex.findFirstMatchIn(_))
    .map(regex => JoinedTeamData(regex.group("player"), Teams.withSafeName(regex.group("team")).getOrElse(Teams.Spectator)))
    .to[collection.immutable.Seq]

  private def setTeams(teams: Seq[JoinedTeamData], players: PlayerStats) : Unit =
    teams.foreach(team => players(team.player).team = team.team)

  private def getSuicides(lines: Array[String]) : Seq[SuicideData] = lines
    .flatMap(suicideRegex.findFirstMatchIn(_))
    .map(regex => SuicideData(regex.group("name")))
    .to[collection.immutable.Seq]

  private def setSuicides(suicides: Seq[SuicideData], players: PlayerStats) : Unit = 
    suicides.foreach(suicide => players(suicide.player).deaths += 1)
}

object ZandronumLogParser {
  def parseLog(source: Source) = new ZandronumLogParser().parseLog(source)
}

case class PlayerData(var team: Teams.Team, var captures: Int, var pCaptures: Int, var drops: Int, var touches: Int, var frags: Int, var deaths: Int) {
  def fragPercentage : Double = if(frags > 0) deaths.toDouble / frags * 100.0 else 100.0
  def capturePercentage : Double = 0.0
  def pickupPercentage : Double = 0.0
  def stopPercentage : Double = 0.0
}

object PlayerData {
  def empty : PlayerData = PlayerData(Teams.Spectator, 0, 0, 0, 0, 0, 0)
}

case class FragData(killer: String, killed: String)
case class SuicideData(player: String)
case class JoinedTeamData(player: String, team: Teams.Team)

object Teams extends Enumeration {
  type Team = Value

  val Spectator, Red, Blue = Value

  def withSafeName(name: String) : Option[Team] = 
    if(Teams.values.exists(element => element.toString == name)) Some(Teams.withName(name))
    else None
}
