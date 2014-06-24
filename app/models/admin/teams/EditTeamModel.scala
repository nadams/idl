package models.admin.teams

import play.api.data._
import play.api.data.format._
import play.api.data.Forms._

case class EditTeamModel(teamId: Int, teamName: String, isActive: Boolean) {
  val isNewTeam = this.teamId == 0
}

object EditTeamModel {
  def empty = EditTeamModel(0, "", true)
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
