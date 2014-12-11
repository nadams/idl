package controllers

import play.api.mvc._
import play.api.mvc.Results._
import components.ProfileComponentImpl

object IdlAction extends ProfileComponentImpl {
  val returnUrlKey = "returnUrl"

  def apply(f: => Request[AnyContent] => Result) = Action { implicit request =>
    request.session.get(SessionKeys.username) map { username =>
      profileService.getByUsername(username) map { profile =>
        if(profile.passwordExpired) Redirect(controllers.routes.ProfileController.forgotPassword.url, Map(IdlAction.returnUrlKey -> Seq(request.uri)))
        else f(request)
      } getOrElse(NotFound("Username was not found"))
    } getOrElse(f(request))
  }
}
