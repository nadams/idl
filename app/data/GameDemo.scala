package data

import org.joda.time.DateTime

case class GameDemo(gameDemoId: Int, gameId: Int, playerId: Int, filename: String, dateUploaded: DateTime)

object GameDemo {
  def apply(x: (Int, Int, Int, String, DateTime)) : GameDemo = 
    GameDemo(x._1, x._2, x._3, x._4, x._5)
}
