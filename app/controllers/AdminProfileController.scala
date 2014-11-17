package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
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

  def search(name: String) = IsAuthenticated { username => implicit request => 
    Ok(Json.toJson(""))
  }
}
