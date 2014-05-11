package controllers

import play.api._
import play.api.mvc._
import components.ProfileComponentImpl
import models.profile._
import _root_.data._
import models.FieldExtensions._
import models.FormExtensions._

object ProfileController extends Controller with Secured with ProvidesHeader with ProfileComponentImpl {
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
      user => Redirect(routes.Application.index).withSession(SessionKeys.username -> user.username)
    )
  }

  def index = IsAuthenticated { username => implicit request =>
    Ok(views.html.profile.index(ProfileModelErrors()))
  }

  def updateProfile = IsAuthenticated { username => implicit request => 
    ChangePasswordForm().bindFromRequest.fold(
      errors => {
        val currentPassword = errors("currentPassword").formattedMessage._2
        val newPassword = errors("newPassword").formattedMessage._2
        val confirmPassword = errors("confirmPassword").formattedMessage._2

        BadRequest(views.html.profile.index(ProfileModelErrors(currentPassword, newPassword, confirmPassword, errors.formattedMessages)))
      },
      result => Redirect(routes.ProfileController.login).withSession(session - SessionKeys.username)
    )
  }

  def logout = Action { implicit request =>
    Redirect(routes.Application.index).withSession(session - SessionKeys.username)
  }
}
