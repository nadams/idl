package controllers

import play.api._
import play.api.mvc._
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
      user => (request.queryString.get(Secured.returnUrl) match {
        case Some(x) => Redirect(x.head)
        case None => Redirect(routes.HomeController.index)
      }) withSession(SessionKeys.username -> user.username)
    )
  }

  def index = IsAuthenticated { username => implicit request =>
    profileService.getByUsername(username) match {
      case Some(profile) => Ok(views.html.profile.index(IndexModel(playerService.profileIsPlayer(profile.profileId))))
      case None => profileNotFound(username)
    }
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
      result => profileService.updateProfilePassword(username, result.newPassword) match {
        case true => Redirect(routes.ProfileController.login).withSession(request.session - SessionKeys.username)
        case false => InternalServerError("Could not update password")
      }
    )
  }

  def logout = Action { implicit request =>
    Redirect(routes.HomeController.index).withSession(request.session - SessionKeys.username)
  }

  def becomePlayer = IsAuthenticated { username => implicit request => 
    profileService.getByUsername(username) match {
      case Some(profile) => playerService.makeProfileAPlayer(profile) match {
        case true => Redirect(routes.ProfileController.index).flashing("profileIsNowPlayer" -> "Congratulations, you are now an IDL player!")
        case false => InternalServerError(
          views.html.profile.index(IndexModel(playerService.profileIsPlayer(profile.profileId)))
        ).flashing("makingProfileAPlayerError" -> "Cannot add you as an IDL player.")
      }
      case None => profileNotFound(username)
    } 
 }

  private def profileNotFound(username: String) = NotFound("The profile `$username` was not found.")
}
