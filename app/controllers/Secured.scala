package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import components._
import security.Roles

trait Secured extends ProfileComponentImpl {
  private def username(request: RequestHeader) = request.session.get(SessionKeys.username)
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.ProfileController.login.url, Map(IdlAction.returnUrlKey -> Seq(request.uri)))

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    IdlAction(request => f(user)(request))
  }

  def IsAuthenticated(roles: Roles.Role*)(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    if(profileService.profileIsInAnyRole(user, roles.toSet)) IdlAction(request => f(user)(request))
    else IdlAction(request => onUnauthorized(request))
  }
}

