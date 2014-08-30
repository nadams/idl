package models.admin.teams

import play.api.libs.json.Json
import data._

case class TeamsModel(teams: Seq[TeamModel])
case class TeamModel(teamId: Int, teamName: String)

object TeamsModel {
  implicit val writesTeamModel = Json.writes[TeamModel]
  implicit val writesTeamsModel = Json.writes[TeamsModel]

  def toModel(teams: Seq[Team]) = TeamsModel(teams.map { team => 
    TeamModel(team.teamId, team.teamName) 
  })
}