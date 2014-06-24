package models.admin.teams

import play.api.libs.json.Json
import data.Team

case class TeamIndexModel(teams: Seq[TeamIndexRowModel])
case class TeamIndexRowModel(teamId: Int, teamName: String, isActive: Boolean)

object TeamIndexModel {
  implicit val writesRowIndexModel = Json.writes[TeamIndexRowModel]
  implicit val writesIndexModel = Json.writes[TeamIndexModel]

  def toModel(teams: Seq[Team]) = TeamIndexModel(teams.map { team => TeamIndexRowModel.toModel(team) })
}

object TeamIndexRowModel {
  def toModel(team: Team) = TeamIndexRowModel(team.teamId, team.name, team.isActive)
}
