package data

import org.joda.time.DateTime

case class GameDemo(gameDemoId: Int, gameId: Int, playerId: Int, filename: String, dateUploaded: DateTime, playerName: String)

object GameDemo {
  def apply(x: (Int, Int, Int, String, DateTime, String)) : GameDemo = 
    GameDemo(x._1, x._2, x._3, x._4, x._5, x._6)
}

case class DemoStatusRecord(playerId: Int, playerName: String, statsUploaded: Boolean, demoDetails: Option[DemoDetailsRecord])
case class DemoDetailsRecord(gameDemoId: Int, filename: String, dateUploaded: DateTime)

object DemoStatusRecord {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val multiRowParser = 
    int(PlayerSchema.playerId) ~
    str(PlayerSchema.name) ~
    bool("StatsUploaded") ~
    get[Option[Int]](GameDemoSchema.gameDemoId) ~
    get[Option[String]](GameDemoSchema.filename) ~
    get[Option[DateTime]](GameDemoSchema.dateUploaded) map flatten *

  def apply(x: (Int, String, Boolean, Option[Int], Option[String], Option[DateTime])) : DemoStatusRecord = 
    x match {
      case (playerId, playerName, statsUploaded, Some(gameDemoId), Some(filename), Some(dateUploaded)) => 
        DemoStatusRecord(playerId, playerName, statsUploaded, Some(DemoDetailsRecord(gameDemoId, filename, dateUploaded)))

      case (playerId, playerName, statsUploaded, None, None, None) => 
        DemoStatusRecord(playerId, playerName, statsUploaded, None)

      case _ => DemoStatusRecord(0, "", false, None)
    }
}
