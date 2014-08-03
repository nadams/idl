package data

import org.joda.time.DateTime

case class GameDemo(gameDemoId: Int, gameId: Int, playerId: Int, filename: String, dateUploaded: DateTime, playerName: String)

object GameDemo {
  def apply(x: (Int, Int, Int, String, DateTime, String)) : GameDemo = 
    GameDemo(x._1, x._2, x._3, x._4, x._5, x._6)
}
