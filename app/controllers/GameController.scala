package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import security.Roles

object GameController extends Controller with ProvidesHeader with Secured {
  def index(id: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok("")
  }
}
