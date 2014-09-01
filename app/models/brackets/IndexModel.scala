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
  class TempStats(val teamId: Int, val teamName: String, var wins: Int, var losses: Int, var ties: Int)

  def toModel(stats: Seq[TeamGameRoundResultRecord]) : Seq[RegularSeasonStatsModel] = {
    val tempStats = stats.map(x => x.teamId -> new TempStats(x.teamId, x.teamName, 0, 0, 0)).toMap[Int, TempStats]

    val groupedStats = stats.groupBy(_.gameId).mapValues(_.groupBy(_.roundId))

    def computeStats(f: Seq[TeamGameRoundResultRecord] => Seq[TeamGameRoundResultRecord]) = 
      groupedStats.flatMap { case (gameId, rounds) => 
        rounds.flatMap { case (roundId, results) => 
          f(results)
        }.groupBy(_.teamId).map(_._2).filter(_.size > 1).map(_.map(_.teamId).toSeq.distinct)
      }

    computeStats { results => 
      val min = results.minBy(_.captures)

      results.filterNot(_.captures == min.captures)
    }.foreach(x => x.foreach(y => tempStats.get(y).map(_.wins += 1)))

    computeStats { results => 
      val max = results.maxBy(_.captures)

      results.filterNot(_.captures == max.captures)
    }.foreach(x => x.foreach(y => tempStats.get(y).map(_.losses += 1)))

    computeStats { results => 
      val max = results.maxBy(_.captures).captures
      val min = results.minBy(_.captures).captures

      results.filter(x => x.captures == max && x.captures == min)
    }.foreach(x => x.foreach(y => tempStats.get(y).map(_.ties += 1)))

    tempStats.values.map(x => RegularSeasonStatsModel(
      x.teamId,
      x.teamName,
      x.wins,
      x.losses,
      x.ties
    )).toSeq
  }
}
