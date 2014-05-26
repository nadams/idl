package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import components._
import models.admin.teams._

object TeamController extends Controller with ProvidesHeader with Secured with SeasonComponentImpl with TeamComponentImpl {
  def index = IsAuthenticated { username => implicit request =>
    Ok(views.html.admin.teams.index(
      TeamIndexModel(
        seasonService.getAllSeasons.map { season => 
          SeasonModel(season.seasonId, season.name)
        },
        Map.empty[String, String]
      )
    ))
  }

  def getTeamList(seasonId: Int) = IsAuthenticated { username => implicit request => 
    Ok(Json.toJson(TeamsModel.toModel(teamService.getTeamsForSeason(seasonId))))
  }

  def getPlayers = IsAuthenticated { username => implicit request => 
    Ok(Json.toJson(PlayersModel.toModel(teamService.getAllPlayers)))
  }

  def assignPlayers = IsAuthenticated { username => implicit request => 
    Ok("")
  }
}
