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

object TeamController extends Controller with ProvidesHeader with Secured with SeasonComponentImpl with TeamComponentImpl {
  def index = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(views.html.admin.teams.index())
  }

  def create = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(views.html.admin.teams.edit(EditTeamModel.empty, EditTeamModelErrors.empty))
  }

  def saveNew = IsAuthenticated(Roles.Admin) { username => implicit request => 
    updateTeam(team => teamService.insertTeam(EditTeamModel.toEntity(team)))
  }

  def edit(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    teamService.getTeam(id) match {
      case Some(team) => Ok(views.html.admin.teams.edit(EditTeamModel.toModel(team), EditTeamModelErrors.empty))
      case None => NotFound(s"No team found with id: $id")
    }
  }

  def saveExisting(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    updateTeam(team => teamService.updateTeam(EditTeamModel.toEntity(team)))
  }

  def remove(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Redirect(routes.TeamController.index)
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

  def getPlayers = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(Json.toJson(PlayersModel.toModel(teamService.getAllPlayers)))
  }

  def assignPlayers = IsAuthenticated(Roles.Admin) { username => implicit request => 
    import models.admin.teams.AssignPlayersToTeamModel._

    handleJsonPost[AssignPlayersToTeamModel](x => Json.toJson(teamService.assignPlayersToTeam(x.teamId, x.playerIds)))
  }

  def removePlayers = IsAuthenticated(Roles.Admin) { username => implicit request =>
    import models.admin.teams.RemovePlayersFromTeamModel._

    handleJsonPost[RemovePlayersFromTeamModel](x => Json.toJson(teamService.removePlayersFromTeam(x.teamId, x.playerIds)))
  }

  private def updateTeam(saveAction: EditTeamModel => Boolean)(implicit request: Request[AnyContent]) : Result = 
    EditTeamForm().bindFromRequest.fold(
      content => {
        val nameError = content("teamName").formattedMessage
        val model = EditTeamModel(0, nameError._1, true)
        val errors = EditTeamModelErrors(nameError._2)

        BadRequest(views.html.admin.teams.edit(model, errors))
      },
      team => {
        saveAction(team) match {
          case true => Redirect(routes.TeamController.index)
          case false => InternalServerError("Could not save team")
        }
      }
    )
}
