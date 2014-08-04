package data

import org.joda.time.DateTime

case class Game(
  gameId: Int, 
  weekId: Int, 
  seasonId: Int, 
  scheduledPlayTime: DateTime, 
  dateCompleted: Option[DateTime],
  teams: Option[(Int, Int)]) {

  lazy val status = dateCompleted.map(date => GameStatus.Completed).getOrElse(GameStatus.Pending)
}

object Game {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  val selectAllSql = 
    s"""
      SELECT
        g.${GameSchema.gameId},
        g.${GameSchema.weekId},
        g.${GameSchema.seasonId},
        g.${GameSchema.scheduledPlayTime},
        g.${GameSchema.dateCompleted},
        tg.${TeamGameSchema.team1Id},
        tg.${TeamGameSchema.team2Id}
      FROM ${GameSchema.tableName} AS g
        LEFT OUTER JOIN ${TeamGameSchema.tableName} as tg on g.${GameSchema.gameId} = tg.${TeamGameSchema.gameId}
    """

  val singleRowParser = 
    int(GameSchema.gameId) ~
    int(GameSchema.weekId) ~
    int(GameSchema.seasonId) ~
    get[DateTime](GameSchema.scheduledPlayTime) ~
    get[Option[DateTime]](GameSchema.dateCompleted) ~
    get[Option[Int]](TeamGameSchema.team1Id) ~ 
    get[Option[Int]](TeamGameSchema.team2Id) map flatten

  val multiRowParser = singleRowParser *

  def apply(data: (Int, Int, Int, DateTime, Option[DateTime], Option[Int], Option[Int])) : Game = 
    if(data._6.isDefined && data._7.isDefined) Game(data._1, data._2, data._3, data._4, data._5, Some(data._6.get, data._7.get))
    else Game(data._1, data._2, data._3, data._4, data._5, None)
}

object GameStatus extends Enumeration {
  type Status = Value

  val Pending, Completed = Value
}
