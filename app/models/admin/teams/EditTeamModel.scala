package models.admin.teams

import org.joda.time.{ DateTime, DateTimeZone }
import play.api.data._
import play.api.data.format._
import play.api.data.Forms._
import _root_.data.Team

case class EditTeamModel(teamId: Int, teamName: String, isActive: Boolean) {
  val isNewTeam = this.teamId == 0
}

object EditTeamModel {
  def empty = EditTeamModel(0, "", true)

  def toModel(team: Team) = EditTeamModel(team.teamId, team.teamName, team.isActive)
  def toEntity(model: EditTeamModel) = Team(model.teamId, model.teamName, model.isActive, new DateTime(DateTimeZone.UTC))
}

case class EditTeamModelErrors(nameError: Option[String])

object EditTeamModelErrors {
  def empty = EditTeamModelErrors(None)
}

object EditTeamForm {
  import formatters.CheckboxFormatter._

  def apply() = 
    Form(
      mapping(
        "teamId" -> number,
        "teamName" -> nonEmptyText,
        "isActive" -> checkbox
      )(EditTeamModel.apply)(EditTeamModel.unapply)
    )
}
