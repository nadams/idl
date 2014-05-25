package controllers

import play.api._
import play.api.mvc._
import components._
import _root_.data._
import models.admin.seasons._
import models.FieldExtensions._
import models.FormExtensions._
import org.joda.time.{ DateTime, DateTimeZone }

object SeasonsController extends Controller with ProvidesHeader with Secured with SeasonComponentImpl {
  def index = IsAuthenticated { username => implicit request => 
    Ok(views.html.admin.seasons.index(SeasonModel.toModels(seasonService.getAllSeasons, routes.SeasonsController)))
  }

  def create = IsAuthenticated { username => implicit request => 
    Ok(views.html.admin.seasons.edit(EditSeasonModel.empty, EditSeasonModelErrors.empty))
  }

  def edit(id: Int) = IsAuthenticated { username => implicit request => 
    seasonService.getSeasonById(id) match {
      case Some(x) => Ok(views.html.admin.seasons.edit(EditSeasonModel.toModel(x), EditSeasonModelErrors.empty))
      case None => Redirect(routes.SeasonsController.create)
    }
  }

  def saveNew = IsAuthenticated { username => implicit request => 
    EditSeasonModelForm().bindFromRequest.fold(
      content => {
        val seasonIdError = content("seasonId").formattedMessage
        val nameError = content("name").formattedMessage
        val startDateError = content("startDate").formattedMessage
        val endDateError = content("endDate").formattedMessage
        val now = new DateTime(DateTimeZone.UTC)

        val editSeasonModel = EditSeasonModel(seasonIdError._1.toInt, nameError._1, now, now)
        val errorsModel = EditSeasonModelErrors(nameError._2, startDateError._2, endDateError._2, content.formattedMessages)

        BadRequest(views.html.admin.seasons.edit(editSeasonModel, errorsModel))
      },
      season => seasonService.insertSeason(Season(season.seasonId, season.name, season.startDate, season.endDate)) match {
        case true => Redirect(routes.SeasonsController.index)
        case false => InternalServerError("Could not save season")
      }
    )
  }

  def saveExisting(id: Int) = IsAuthenticated { username => implicit request => 
    Ok("Temp")
  }

  def remove(id: Int) = TODO
}
