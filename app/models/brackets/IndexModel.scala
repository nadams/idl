package models.brackets

import play.api.libs.json.Json
import data._

case class IndexModel(playoffStats: Seq[PlayoffStatsModel], regularStats: Seq[RegularSeasonStatsModel])
case class PlayoffStatsModel(teamId: Int, teamName: String, gameId: Int, weekId: Int, captures: Int)
case class RegularSeasonStatsModel(teamId: Int, teamName: String, wins: Int, losses: Int, ties: Int)

object IndexModel {
  implicit val writesRegularSeasonStatsModel = Json.writes[RegularSeasonStatsModel]
  implicit val writesPlayoffStatsModel = Json.writes[PlayoffStatsModel]
  implicit val writesIndexModel = Json.writes[IndexModel]

  def toModel(stats: Seq[TeamGameRoundResultRecord]) = IndexModel(
    stats.filter(_.gameTypeId == GameTypes.Playoff.id).map(PlayoffStatsModel.toModel(_)),
    RegularSeasonStatsModel.toModel(stats.filter(_.gameTypeId == GameTypes.Regular.id))
  )
}

object PlayoffStatsModel {
  def toModel(data: TeamGameRoundResultRecord) = 
    PlayoffStatsModel(data.teamId, data.teamName, data.gameId, data.weekId, data.captures)
}

object RegularSeasonStatsModel {
  class TeamStats(var teamId: Int, var wins: Int, var losses: Int, var ties: Int)
  class TempStats(val teamId: Int, val teamName: String, var wins: Int, var losses: Int, var ties: Int)

  def toModel(stats: Seq[TeamGameRoundResultRecord]) : Seq[RegularSeasonStatsModel] = {
    val tempStats = stats.map(x => x.teamId -> new TempStats(x.teamId, x.teamName, 0, 0, 0)).toMap[Int, TempStats]

    val groupedStats = stats.groupBy(_.gameId).mapValues(_.groupBy(_.roundId))

    groupedStats.foreach { game => 
      var team1Stats = new TeamStats(0, 0, 0, 0)
      var team2Stats = new TeamStats(0, 0, 0, 0)
    
      game._2.foreach { round => 
        val team1 = round._2(0)
        val team2 = round._2(1)
        
        team1Stats.teamId = team1.teamId
        team2Stats.teamId = team2.teamId

        if(team1.captures > team2.captures) {
          team1Stats.wins += 1
          team2Stats.losses += 1
        } else if(team2.captures > team1.captures) {
          team2Stats.wins += 1
          team1Stats.losses += 1
        } else {
          team2Stats.ties += 1
          team1Stats.ties += 1
        }
      }
      
      if(team1Stats.wins > team2Stats.wins) {
        tempStats(team1Stats.teamId).wins += 1
        tempStats(team2Stats.teamId).losses += 1
      } else if(team2Stats.wins > team1Stats.wins) {
    	  tempStats(team1Stats.teamId).losses += 1
        tempStats(team2Stats.teamId).wins += 1
      } else {
    	  tempStats(team1Stats.teamId).ties += 1
        tempStats(team2Stats.teamId).ties += 1
      }
    }

    tempStats.values.map(x => RegularSeasonStatsModel(
      x.teamId,
      x.teamName,
      x.wins,
      x.losses,
      x.ties
    )).toSeq
  }
}
