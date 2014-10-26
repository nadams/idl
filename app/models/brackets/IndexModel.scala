package models.brackets

import collection.mutable.{ Map => MutableMap }
import play.api.libs.json.Json
import data._

case class IndexModel(playoffStats: PlayoffStatsModel, regularStats: Seq[RegularSeasonStatsModel])
case class PlayoffTeamStatsModel(teamId: Int, teamName: String, wins: Int)
case class PlayoffGameStatsModel(gameId: Int, weekId: Int, teamStats: Seq[PlayoffTeamStatsModel])
case class PlayoffStatsModel(results: Seq[PlayoffGameStatsModel])
case class RegularSeasonStatsModel(teamId: Int, teamName: String, wins: Int, losses: Int, ties: Int)

class TeamStats(var teamId: Int, var wins: Int, var losses: Int, var ties: Int)
class TempStats(val teamId: Int, val teamName: String, var wins: Int, var losses: Int, var ties: Int)

object IndexModel {
  implicit val writesRegularSeasonStatsModel = Json.writes[RegularSeasonStatsModel]
  implicit val writesPlayoffTeamStatsModel = Json.writes[PlayoffTeamStatsModel]
  implicit val writesPlayoffGameStatsModel = Json.writes[PlayoffGameStatsModel]
  implicit val writesPlayoffStatsModel = Json.writes[PlayoffStatsModel]
  implicit val writesIndexModel = Json.writes[IndexModel]

  def toModel(stats: Seq[TeamGameRoundResultRecord]) = IndexModel(
    PlayoffStatsModel.toModel(stats.filter(_.gameTypeId == GameTypes.Playoff.id)),
    RegularSeasonStatsModel.toModel(stats.filter(_.gameTypeId == GameTypes.Regular.id))
  )
}

object PlayoffStatsModel {
  def toModel(stats: Seq[TeamGameRoundResultRecord]) = {
    val groupedStats: Map[Int, Map[Int, Seq[TeamGameRoundResultRecord]]] = stats.groupBy(_.gameId).mapValues(_.groupBy(_.roundId))
    val gameStats = MutableMap[Int, MutableMap[Int, Int]]()
    groupedStats.foreach { case(gameId, rounds) => 
      val teamStats = gameStats.getOrElseUpdate(gameId, MutableMap[Int, Int]())

      rounds.foreach { case(roundId, stats) =>
        val team1 = stats(0)
        val team2 = stats(1)
        
        if(team1.captures > team2.captures) {
          val wins = teamStats.getOrElseUpdate(team1.teamId, 0)
          teamStats(team1.teamId) = wins + 1
        } else if(team2.captures > team1.captures) {
          val wins = teamStats.getOrElseUpdate(team2.teamId, 0)
          teamStats(team2.teamId) = wins + 1
        }
      }
    }
    
    PlayoffStatsModel(
      gameStats.map { case(gameId, rounds) =>
        stats.find(_.gameId == gameId).map { game => 
          PlayoffGameStatsModel(
            game.gameId, 
            game.weekId,
            rounds.map { case(teamId, wins) => 
              stats.find(_.teamId == teamId).map(team => PlayoffTeamStatsModel(team.teamId, team.teamName, wins)).getOrElse(PlayoffTeamStatsModel(0, "", 0))
            }.toSeq
          ) 
        }.getOrElse(PlayoffGameStatsModel(0, 0, Seq.empty[PlayoffTeamStatsModel]))
      }.toSeq
    )
  }
}

object RegularSeasonStatsModel {
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
