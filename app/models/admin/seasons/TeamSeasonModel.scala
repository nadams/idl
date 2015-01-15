package models.admin.seasons

import play.api.libs.json.Json
import data._
import models.admin.teams.TeamModel

case class TeamSeasonsModel(seasonId: Int, seasonName: String, allTeams: Seq[TeamModel], assignedTeams: Seq[Int])

object TeamSeasonsModel {
  import models.admin.teams.TeamsModel._

  implicit val writesTeamSeasonsModel = Json.writes[TeamSeasonsModel]
  lazy val empty : TeamSeasonsModel = TeamSeasonsModel(0, "", Seq.empty[TeamModel], Seq.empty[Int])

  def toModel(season: Season, allTeams: Seq[Team], teamsInSeason: Seq[Team]) : TeamSeasonsModel = 
    TeamSeasonsModel(season.seasonId, season.name, allTeams.map { team => TeamModel(team.teamId, team.teamName) }, teamsInSeason.map(_.teamId))
}
