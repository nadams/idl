package controllers

import play.api._
import play.api.mvc._

object TeamsController extends Controller with ProvidesHeader {
  def index = Action { implicit request =>
    Ok(views.html.admin.teams.index())
  }
}
