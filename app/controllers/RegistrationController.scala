package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

object RegistrationController extends Controller with ProvidesHeader {
  def index = Action { implicit request => 
    Ok(views.html.registration.index())
  }
}