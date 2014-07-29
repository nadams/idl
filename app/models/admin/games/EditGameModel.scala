package models.admin.games

import play.api.libs.json._

case class EditGameModel(gameId: Int, availableTeams: Seq[TeamModel], availableWeeks: Seq[WeekModel]) {
  val isNewGame = gameId == 0
}

object EditGameModel {
  def empty = EditGameModel(0, Seq.empty[TeamModel], Seq.empty[WeekModel])
}

case class TeamModel(teamId: Int, teamName: Int)

object TeamModel {
  implicit val writesTeamModel = Json.writes[TeamModel]
}

case class WeekModel(weekId: Int, weekName: Int)

object WeekModel {
  implicit val writesWeekModel = Json.writes[WeekModel]
}
