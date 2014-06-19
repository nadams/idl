package models.admin.teams

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
