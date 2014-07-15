package data

import org.joda.time.DateTime

case class Game(
  gameId: Int, 
  weekId: Int, 
  seasonId: Int, 
  scheduledPlayTime: DateTime, 
  dateCompleted: Option[DateTime],
  teams: Option[(Int, Int)]
)

object Game {
  def apply(data: (Int, Int, Int, DateTime, Option[DateTime], Option[Int], Option[Int])) : Game = 
    if(data._6.isDefined && data._7.isDefined) Game(data._1, data._2, data._3, data._4, data._5, Some(data._6.get, data._7.get))
    else Game(data._1, data._2, data._3, data._4, data._5, None)
}
