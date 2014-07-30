package models.admin.games

import play.api.data._
import play.api.data.Forms._
import org.joda.time.{ DateTime, DateTimeZone }
import _root_.data._
import components.TeamComponentImpl

case class EditGameModel(gameId: Int, availableTeams: Seq[TeamModel], availableWeeks: Seq[WeekModel], selectedWeekId: Int, selectedTeam1Id: Int, selectedTeam2Id: Int) {
  lazy val isNewGame = gameId == 0
  lazy val hasWeekSelected = selectedWeekId != 0
  lazy val hasTeam1Selected = selectedTeam1Id != 0
  lazy val hasTeam2Selected = selectedTeam2Id != 0
}

object EditGameModel extends TeamComponentImpl {
  def toModel(seasonId: Int, gameId: Int, selectedWeekId: Int, selectedTeam1Id: Int, selectedTeam2Id: Int) = 
    EditGameModel(
      gameId, 
      teamService.getTeamsForSeason(seasonId).map(team => TeamModel(team.teamId, team.name)),
      WeekModel.enumerate,
      selectedWeekId,
      selectedTeam1Id,
      selectedTeam2Id
    )
}

case class EditGamePostModel(gameId: Int, selectedWeekId: Int, selectedTeam1Id: Int, selectedTeam2Id: Int) {
  def toEntity(seasonId: Int) = Game(
    gameId, 
    selectedWeekId, 
    seasonId, 
    new DateTime(DateTimeZone.UTC), 
    None, 
    Some((selectedTeam1Id, selectedTeam2Id))
  )
}
  
object EditGameForm {
  def apply() = Form(
    mapping(
      "gameId" -> number,
      "selectedWeekId" -> number,
      "selectedTeam1Id" -> number,
      "selectedTeam2Id" -> number
    )(EditGamePostModel.apply)(EditGamePostModel.unapply)
    verifying("error.teamSelectionRequired", result => result.selectedTeam1Id != 0 && result.selectedTeam2Id != 0)
    verifying("error.teamsCannotBeSame", result => result.selectedTeam1Id != result.selectedTeam2Id)
  )
}

case class EditGameErrors(gameIdError: Option[String], weekError: Option[String], team1Error: Option[String], team2Error: Option[String], globalErrors: Seq[String])

object EditGameErrors {
  def empty = EditGameErrors(None, None, None, None, Seq.empty[String])
}

case class TeamModel(teamId: Int, teamName: String)
case class WeekModel(weekId: Int, weekName: String)

object WeekModel {
  def enumerate = Weeks.values.map(week => WeekModel(week.id, week.toString)).toSeq.sortBy(x => x.weekId)
}
