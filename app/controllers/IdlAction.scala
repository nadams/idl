package controllers

import play.api.mvc._
import play.api.mvc.Results._
import components.ProfileComponentImpl

object IdlAction extends ProfileComponentImpl {
  def apply(username: Option[String] = None)(f: => Request[AnyContent] => Result) = Action { implicit request =>
    username map { x =>
      profileService.getByUsername(x) map { profile =>
        if(profile.passwordExpired) Redirect(controllers.routes.ProfileController.forgotPassword)
        else f(request)
      } getOrElse(NotFound("Username was not found"))
    } getOrElse(f(request))
  }
}
