package controllers

import play.api._
import play.api.mvc._

object HomeController extends Controller with ProvidesHeader {
  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }
}