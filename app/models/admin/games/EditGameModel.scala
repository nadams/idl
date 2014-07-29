package models.admin.games

import data._

case class EditGameModel(gameId: Int, availableTeams: Seq[TeamModel], availableWeeks: Seq[WeekModel]) {
  val isNewGame = gameId == 0
}

object EditGameModel {
  def empty = EditGameModel(0, Seq.empty[TeamModel], Seq.empty[WeekModel])

  def toModel(gameId: Int, teams: Seq[Team]) = 
    EditGameModel(
      gameId, 
      teams.map(team => TeamModel(team.teamId, team.name)),
      WeekModel.enumerate
    )
}

case class TeamModel(teamId: Int, teamName: String)
case class WeekModel(weekId: Int, weekName: String)

object WeekModel {
  def enumerate = Weeks.values.map(week => WeekModel(week.id, week.toString)).toSeq.sortBy(x => x.weekId)
}
