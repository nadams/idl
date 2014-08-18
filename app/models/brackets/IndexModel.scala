package models.brackets

import play.api.libs.json.Json
import data._

case class IndexModel(playoffStats: Seq[PlayoffStatsModel], regularStats: Seq[RegularSeasonStatsModel])
case class PlayoffStatsModel(teamId: Int, teamName: String, gameId: Int, weekId: Int, captures: Int)
case class RegularSeasonStatsModel(teamId: Int, teamName: String, gameId: Int, wins: Int, losses: Int, ties: Int)

object IndexModel {
  implicit val writesRegularSeasonStatsModel = Json.writes[RegularSeasonStatsModel]
  implicit val writesPlayoffStatsModel = Json.writes[PlayoffStatsModel]
  implicit val writesIndexModel = Json.writes[IndexModel]

  def toModel(stats: Seq[TeamGameResultRecord]) = IndexModel(
    stats.map(PlayoffStatsModel.toModel(_)),
    RegularSeasonStatsModel.toModel(stats)
  )
}

object PlayoffStatsModel {
  def toModel(data: TeamGameResultRecord) = 
    PlayoffStatsModel(data.teamId, data.teamName, data.gameId, data.weekId, data.captures)
}

object RegularSeasonStatsModel {
  class TempStats(val teamId: Int, val teamName: String, val gameId: Int, var wins: Int, var losses: Int, var ties: Int)

  def toModel(stats: Seq[TeamGameResultRecord]) : Seq[RegularSeasonStatsModel] = {
    val winners = stats.groupBy(_.gameId).mapValues { x =>
      val max = x.maxBy(_.captures).captures
      x.filter(y => y.captures == max)
    }.filter(_._2.size == 1)

    val losers = stats.groupBy(_.gameId).mapValues { x =>
      val min = x.minBy(_.captures).captures
      x.filter(y => y.captures == min)
    }.filter(_._2.size == 1)
    
    val ties = stats.groupBy(_.gameId)
      .mapValues(_.groupBy(_.captures).values.filter(_.size > 1).flatMap(x => x).toList)
      .filter(_._2.size > 1)

    val models = scala.collection.mutable.List[RegularSeasonStatsModel]();
    modes ++ winners.values.groupBy(_.teamId).map(_._2.size)



    models
  }

  def join[A, B, K, R](outer: Traversable[A], inner: Traversable[B])(outKey: A => K, inKey: B => K, f: (A, B) => R): Traversable[R] = {
    for(o <- outer; i <- inner; if outKey(o) == inKey(i)) yield f(o, i)
  }
}



