package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import components._
import _root_.data._
import models.registration._

object RegistrationController extends Controller with ProvidesHeader with ProfileComponentImpl {
  def index = Action { implicit request => 
    Ok(views.html.registration.index(RegisterModel.empty, RegisterModelErrors.empty))
  }

  def register = Action { implicit request =>
    RegisterModelForm().bindFromRequest.fold(
      errors => {
        Logger.info(errors.toString)
        BadRequest(views.html.registration.index(RegisterModel.toModel(errors), RegisterModelErrors.toModel(errors)))
      },
      model => profileService.createProfile(model.email, model.email, model.password) match {
        case Profile(profileId, email, displayName, password, passwordExpired, dateCreated, lastLoginDate) => 
          Redirect(routes.HomeController.index).withSession(SessionKeys.username -> email)
        case _ => InternalServerError("Unable to create profile")
      }
    )
  }
}