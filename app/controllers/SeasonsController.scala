package controllers

import play.api._
import play.api.mvc._

object SeasonsController extends Controller with ProvidesHeader {
  def index = Action { implicit request => 
    Ok(views.html.admin.seasons.index())
  }
}