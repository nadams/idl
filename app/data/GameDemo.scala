package data

import org.joda.time.DateTime

case class GameDemo(gameDemoId: Int, gameId: Int, playerId: Int, filename: String, dateUploaded: DateTime)

object GameDemo {
  def apply(x: (Int, Int, Int, String, DateTime)) : GameDemo = 
    GameDemo(x._1, x._2, x._3, x._4, x._5)
}

case class DemoStatusRecord(playerId: Int, playerName: String, demoDetails: Option[DemoDetailsRecord])
case class DemoDetailsRecord(gameDemoId: Int, filename: String, dateUploaded: DateTime)

object DemoStatusRecord {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

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
