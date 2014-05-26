package controllers

import play.api._
import play.api.mvc._
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
    Ok("")
  }
}
