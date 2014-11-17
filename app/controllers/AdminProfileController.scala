package controllers

import play.api._
import play.api.mvc._
import components._
import security.Roles

object AdminProfileController extends Controller 
  with ProvidesHeader 
  with Secured
  with ProfileComponentImpl 
  with TeamComponentImpl
  with PlayerComponentImpl {

  def index = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok(views.html.admin.profile.index())
  }
}
