package controllers

import scala.util.{ Try, Success, Failure }
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

  private val profileNotFound = NotFound("Profile not found")

  def index = IsAuthenticated(Roles.Admin) { username => implicit request =>
    Ok(views.html.admin.profile.index())
  }

  def profile(profileId: Int) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    profileService.getById(profileId) map { profile =>
      val players = playerService.getPlayersByProfileId(profile.profileId)
      val roles = profileService.getRolesForUsername(username)

      Ok(views.html.admin.profile.profile(ProfileInformationModel.toModel(profile, roles, players)))
    } getOrElse(profileNotFound)
  }

  def search(name: String) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    import ProfileSearchModel._

    Ok(Json.toJson(ProfileSearchModel.toModel(profileService.searchProfiles(name))))
  }

  def updateProfile(profileId: Int) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    import UpdateProfileModel._

    handleJsonPost[UpdateProfileModel] { model =>
      profileService.getById(profileId) map { profile =>
        val updatedProfile = profile.copy(
          displayName = model.displayName,
          email = model.email
        )

        profileService.updateProfile(updatedProfile) map { result =>
          Ok(Json.toJson(UpdateProfileModel.toModel(result)))
        } getOrElse(InternalServerError("Could not update profile information"))
      } getOrElse(profileNotFound)
    }
  }

  def unapprovePlayer(profileId: Int) = playerAction(profileId, playerService.unapprovePlayer)
  def approvePlayer(profileId: Int) = playerAction(profileId, playerService.approvePlayer)
  def removePlayer(profileId: Int) = playerAction(profileId, playerService.removePlayer)
  def addRoles(profileId: Int) = rolesAction(profileId, profileService.addProfileToRoles)
  def removeRoles(profileId: Int) = rolesAction(profileId, profileService.removeProfileFromRoles)

  private def rolesAction(profileId: Int, serviceMethod: (Int, Seq[Int]) => Seq[Int]) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    import AlterRolesModel._

    handleJsonPost[AlterRolesModel] { model =>
      profileService.getById(profileId) map { profile =>
        Ok(Json.toJson(AlterRolesModel.toModel(serviceMethod(profile.profileId, model.roleIds))))
      } getOrElse(profileNotFound)
    }
  }

  private def playerAction(profileId: Int, serviceMethod: (Int, Int) => Try[Int]) = IsAuthenticated(Roles.Admin) { username => implicit request =>
    import AlterPlayerModel._

    handleJsonPost[AlterPlayerModel] { model =>
      serviceMethod(profileId, model.playerId) match {
        case Success(result) => Ok(Json.toJson(AlterPlayerModel.toModel(result)))
        case Failure(ex) => InternalServerError(ex.toString)
      }
    }
  }
}
