package models.admin.teams

import play.api.libs.json.Json
import data.Team

case class TeamIndexModel(teams: Seq[TeamIndexRowModel])
case class TeamIndexRowModel(teamId: Int, teamName: String, isActive: Boolean, editUrl: String)

object TeamIndexModel {
  implicit val writesRowIndexModel = Json.writes[TeamIndexRowModel]
  implicit val writesIndexModel = Json.writes[TeamIndexModel]

  def toModel(teams: Seq[Team], routes: controllers.ReverseTeamController) = TeamIndexModel(
    teams.map { team => TeamIndexRowModel.toModel(team, routes) }
  )
}

object TeamIndexRowModel {
  def toModel(team: Team, routes: controllers.ReverseTeamController) = 
    TeamIndexRowModel(
      team.teamId, 
      team.teamName, 
      team.isActive, 
      routes.edit(team.teamId).toString
    )
}
