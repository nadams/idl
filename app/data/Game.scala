package data

import org.joda.time.DateTime

case class Game(
  gameId: Int, 
  weekId: Int, 
  seasonId: Int, 
  scheduledPlayTime: DateTime, 
  dateCompleted: DateTime,
  team1: Option[Team],
  team2: Option[Team]
)

object Game {

}
