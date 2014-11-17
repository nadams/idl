package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import components._
import models.admin.profile._
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

  def search(name: String) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    import ProfileSearchModel._

    Ok(Json.toJson(ProfileSearchModel.toModel(profileService.searchProfiles(name))))
  }
}
