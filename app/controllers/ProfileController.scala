package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import components.{ ProfileComponentImpl, PlayerComponentImpl, TeamComponentImpl }
import models.profile._
import _root_.data._
import models.FieldExtensions._
import models.FormExtensions._

object ProfileController 
  extends Controller 
  with Secured 
  with ProvidesHeader 
  with ProfileComponentImpl 
  with TeamComponentImpl 
  with PlayerComponentImpl {

  def login = Action { implicit request =>
    Ok(views.html.profile.login(LoginModel(), Seq.empty[String]))
  }

  def performLogin = Action { implicit request =>
    LoginForm().bindFromRequest.fold(
      errors => {
        val loginModel = LoginModel(errors("username").formattedMessage._1, "")
        val errorMessages = errors.formattedMessages

        BadRequest(views.html.profile.login(loginModel, errorMessages))
      },
      user => request.queryString.get(Secured.returnUrl).fold(Redirect(routes.HomeController.index))(url => Redirect(url.head))
        .withSession(SessionKeys.username -> user.username)
    )
  }

  def index = IsAuthenticated { username => implicit request =>
    profileService.getByUsername(username).fold(profileNotFound(username))(profile =>
      Ok(views.html.profile.index(IndexModel(profile.profileId, playerService.profileIsPlayer(profile.profileId))))
    )
  }

  def password = IsAuthenticated { username => implicit request => 
    Ok(views.html.profile.password(ProfileModelErrors()))
  }

  def updatePassword = IsAuthenticated { username => implicit request => 
    ChangePasswordForm().bindFromRequest.fold(
      errors => {
        val currentPassword = errors("currentPassword").formattedMessage._2
        val newPassword = errors("newPassword").formattedMessage._2
        val confirmPassword = errors("confirmPassword").formattedMessage._2

        BadRequest(views.html.profile.password(ProfileModelErrors(currentPassword, newPassword, confirmPassword, errors.formattedMessages)))
      },
      result => 
        if(profileService.updateProfilePassword(username, result.newPassword)) {
          Redirect(routes.ProfileController.login).withSession(request.session - SessionKeys.username)
        } else {
          InternalServerError("Could not update password")
        }
    )
  }

  def logout = Action { implicit request =>
    Redirect(routes.HomeController.index).withSession(request.session - SessionKeys.username)
  }

  def becomePlayer = IsAuthenticated { username => implicit request => 
    profileService.getByUsername(username).fold(profileNotFound(username))(profile => 
      if(playerService.makeProfileAPlayer(profile)) {
        Ok(Json.toJson(BecomePlayerResultModel(true, "Congratulations, you are now an IDL player!")))
      } else {
        InternalServerError(Json.toJson(BecomePlayerResultModel(false, "Cannot add you as an IDL player.")))
      }
    )
  }

  def myGames = IsAuthenticated { username => implicit request => 
    Ok(views.html.profile.myGames())
  }

  private def profileNotFound(username: String) = NotFound("The profile `$username` was not found.")
}
