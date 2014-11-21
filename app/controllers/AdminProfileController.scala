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

  def profile(profileId: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    profileService.getById(profileId) map { profile => 
      val players = playerService.getPlayersByProfileId(profile.profileId)
      
      Ok(views.html.admin.profile.profile(ProfileInformationModel.toModel(profile, players)))
    } getOrElse(NotFound("Profile not found"))
  }

  def search(name: String) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    import ProfileSearchModel._

    Ok(Json.toJson(ProfileSearchModel.toModel(profileService.searchProfiles(name))))
  }

  def updateProfile(profileId: Int) = IsAuthenticated(Roles.Admin) { username => implicit request => 
    Ok("")
  }
}
