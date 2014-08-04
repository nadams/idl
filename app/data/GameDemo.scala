package data

import anorm._ 
import anorm.SqlParser._
import AnormExtensions._
import org.joda.time.DateTime

case class GameDemo(gameDemoId: Int, gameId: Int, playerId: Int, filename: String, dateUploaded: DateTime)

object GameDemo {

  lazy val selectAllSql = 
    s"""
      SELECT 
        ${GameDemoSchema.gameDemoId},
        ${GameDemoSchema.gameId},
        ${GameDemoSchema.playerId},
        ${GameDemoSchema.filename},
        ${GameDemoSchema.dateUploaded}
      FROM ${GameDemoSchema.tableName}
    """

  lazy val singleRowParser = 
    int(GameDemoSchema.gameDemoId) ~
    int(GameDemoSchema.gameId) ~
    int(GameDemoSchema.playerId) ~
    str(GameDemoSchema.filename) ~
    get[DateTime](GameDemoSchema.dateUploaded) ~
    str(PlayerSchema.name) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, Int, Int, String, DateTime)) : GameDemo = 
    GameDemo(x._1, x._2, x._3, x._4, x._5)
}

case class DemoStatusRecord(playerId: Int, playerName: String, demoDetails: Option[DemoDetailsRecord])
case class DemoDetailsRecord(gameDemoId: Int, filename: String, dateUploaded: DateTime)

object DemoStatusRecord {
  lazy val multiRowParser = 
    int(PlayerSchema.playerId) ~
    str(PlayerSchema.name) ~
    get[Option[Int]](GameDemoSchema.gameDemoId) ~
    get[Option[String]](GameDemoSchema.filename) ~
    get[Option[DateTime]](GameDemoSchema.dateUploaded) map flatten *

  def apply(x: (Int, String, Option[Int], Option[String], Option[DateTime])) : DemoStatusRecord = 
    x match {
      case (playerId, playerName, Some(gameDemoId), Some(filename), Some(dateUploaded)) => 
        DemoStatusRecord(playerId, playerName, Some(DemoDetailsRecord(gameDemoId, filename, dateUploaded)))

      case (playerId, playerName, None, None, None) => 
        DemoStatusRecord(playerId, playerName, None)

      case _ => DemoStatusRecord(0, "", None)
    }
}
