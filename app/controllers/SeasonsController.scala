package controllers

import play.api._
import play.api.mvc._
import components._
import _root_.data._
import models.admin.seasons._

object SeasonsController extends Controller with ProvidesHeader with Secured with SeasonComponentImpl {
  def index = IsAuthenticated { username => implicit request => 
    Ok(views.html.admin.seasons.index(SeasonModel.toModels(seasonService.getAllSeasons, routes.SeasonsController)))
  }

  def edit(id: Int) = TODO
  def remove(id: Int) = TODO
}
