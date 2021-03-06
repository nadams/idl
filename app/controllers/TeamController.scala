package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import org.joda.time.{ DateTime, DateTimeZone }
import components._
import _root_.data._
import models.admin.teams._
import models.FieldExtensions._
import models.FormExtensions._
import security.Roles

object TeamController extends Controller with ProvidesHeader with Secured with SeasonComponentImpl with TeamComponentImpl with PlayerComponentImpl {
  def index = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(views.html.admin.teams.index(TeamIndexModel.toModel(teamService.getAllTeams, routes.TeamController)))
  }

  def create = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(views.html.admin.teams.edit(EditTeamModel.empty, EditTeamModelErrors.empty))
  }

  def saveNew = IsAuthenticated(Roles.Admin) { username => implicit request => 
    updateTeam(team => teamService.insertTeam(EditTeamModel.toEntity(team)))
  }

  def edit(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    teamService.getTeam(id)
      .map(team => Ok(views.html.admin.teams.edit(EditTeamModel.toModel(team), EditTeamModelErrors.empty)))
      .getOrElse(NotFound(s"No team found with id: $id"))
  }

  def saveExisting(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    updateTeam(team => teamService.updateTeam(EditTeamModel.toEntity(team)))
  }

  def remove(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    if(teamService.removeTeam(id)) Ok(Json.toJson(id))
    else InternalServerError("Could not remove team")
  }

  def players = IsAuthenticated(Roles.Admin) { username => implicit request =>
    Ok(views.html.admin.teams.players(
      TeamPlayersModel(
        seasonService.getAllSeasons.map { season => 
          SeasonModel(season.seasonId, season.name)
        },
        Map.empty[String, String]
      )
    ))
  }

  def getTeamList(seasonId: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(Json.toJson(TeamsModel.toModel(teamService.getTeamsForSeason(seasonId))))
  }

  def getPlayers() = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(Json.toJson(PlayersModel.toModel(playerService.getPlayers())))
  }

  def assignPlayers = IsAuthenticated(Roles.Admin) { username => implicit request => 
    import models.admin.teams.AssignPlayersToTeamModel._

    handleJsonPost[AssignPlayersToTeamModel](x => Ok(Json.toJson(teamService.assignPlayersToTeam(x.teamId, x.playerIds))))
  }

  def removePlayers = IsAuthenticated(Roles.Admin) { username => implicit request =>
    import models.admin.teams.RemovePlayersFromTeamModel._

    handleJsonPost[RemovePlayersFromTeamModel](x => Ok(Json.toJson(teamService.removePlayersFromTeam(x.teamId, x.playerIds))))
  }
  
  def makeCaptain(teamId: Int, playerId: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(Json.toJson(teamService.makeCaptain(teamId, playerId))) 
  }  

  private def updateTeam(saveAction: EditTeamModel => Boolean)(implicit request: Request[AnyContent]) : Result = 
    EditTeamForm().bindFromRequest.fold(
      content => {
        val nameError = content("teamName").formattedMessage
        val model = EditTeamModel(0, nameError._1, true)
        val errors = EditTeamModelErrors(nameError._2)

        BadRequest(views.html.admin.teams.edit(model, errors))
      },
      team => 
        if(saveAction(team)) Redirect(routes.TeamController.index)
        else InternalServerError("Could not save team")
    )
}
