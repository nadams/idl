package controllers

import play.api._
import play.api.mvc._
import models.admin.teams._

object TeamController extends Controller with ProvidesHeader with Secured {
  def index = IsAuthenticated { username => implicit request =>
    Ok(views.html.admin.teams.index(TeamIndexModel.empty))
  }
}
