package controllers

import play.api._
import play.api.mvc._
import org.joda.time.{ DateTime, DateTimeZone }
import components._
import _root_.data._
import models.admin.seasons._
import models.FieldExtensions._
import models.FormExtensions._
import extensions.DateTimeExtensions._

object SeasonController extends Controller with ProvidesHeader with Secured with SeasonComponentImpl {
  def index = IsAuthenticated { username => implicit request => 
    Ok(views.html.admin.seasons.index(SeasonModel.toModels(seasonService.getAllSeasons, routes.SeasonController)))
  }

  def create = IsAuthenticated { username => implicit request => 
    Ok(views.html.admin.seasons.edit(EditSeasonModel.empty, EditSeasonModelErrors.empty))
  }

  def edit(id: Int) = IsAuthenticated { username => implicit request => 
    seasonService.getSeasonById(id) match {
      case Some(x) => Ok(views.html.admin.seasons.edit(EditSeasonModel.toModel(x), EditSeasonModelErrors.empty))
      case None => Redirect(routes.SeasonController.create)
    }
  }

  def saveNew = IsAuthenticated { username => implicit request => 
    updateSeason(season => seasonService.insertSeason(Season(season.seasonId, season.name, season.startDate, season.endDate)))
  }

  def saveExisting(id: Int) = IsAuthenticated { username => implicit request => 
    updateSeason(season => seasonService.updateSeason(season.seasonId, season.name, season.startDate, season.endDate))
  }

  def remove(id: Int) = IsAuthenticated { username => implicit request => 
    seasonService.removeSeason(id) match {
      case true => Redirect(routes.SeasonController.index)
      case false => Redirect(routes.SeasonController.index).flashing("couldNotRemoveSeason" -> "Could not remove season")
    }
  }

  def updateSeason(saveAction: EditSeasonModel => Boolean)(implicit request: Request[AnyContent]) : Result = 
    EditSeasonModelForm().bindFromRequest.fold(
      content => {
        val seasonIdError = content("seasonId").formattedMessage
        val nameError = content("name").formattedMessage
        val startDateError = content("startDate").formattedMessage
        val endDateError = content("endDate").formattedMessage

        val editSeasonModel = EditSeasonModel(seasonIdError._1.toInt, nameError._1, startDateError._1.toDateTime, endDateError._1.toDateTime)
        val errorsModel = EditSeasonModelErrors(nameError._2, startDateError._2, endDateError._2, content.formattedMessages)

        BadRequest(views.html.admin.seasons.edit(editSeasonModel, errorsModel))
      },
      season => saveAction(season) match {
        case true => Redirect(routes.SeasonController.index)
        case false => InternalServerError("Could not save season")
      }
    )
}
