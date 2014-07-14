package data

import org.joda.time.DateTime

case class Game(
  gameId: Int, 
  weekId: Int, 
  seasonId: Int, 
  scheduledPlayTime: DateTime, 
  dateCompleted: DateTime,
  team1: Option[Int],
  team2: Option[Int]
)

object Game {
  def apply(data: (Int, Int, Int, DateTime, DateTime, Option[Int], Option[Int])) : Game = 
    Game(data._1, data._2, data._3, data._4, data._5, data._6, data._6)
}
