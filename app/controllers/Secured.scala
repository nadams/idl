package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import components._
import security.Roles

trait Secured extends ProfileComponentImpl {
  private def username(request: RequestHeader) = request.session.get(SessionKeys.username)
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.ProfileController.login.url, Map(Secured.returnUrl -> Seq(request.uri)))

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user => 
    Action(request => f(user)(request))
  }

  def IsAuthenticated(roles: Roles.Role*)(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user => 
    if(profileService.profileIsInAnyRole(user, roles.toSet)) {
      Action(request => f(user)(request))
    } else {
      Action(request => onUnauthorized(request))
    }
  }
}

object Secured {
  val returnUrl = "returnUrl"
}
