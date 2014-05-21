package controllers

import play.api._
import play.api.mvc._
import components._
import data._
import models.admin.seasons._

object SeasonsController extends Controller with ProvidesHeader with Secured with SeasonComponentImpl {
  def index = IsAuthenticated { username => implicit request => 
    Ok(views.html.admin.seasons.index(Seasons.empty))
  }
}