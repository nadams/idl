package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import components._
import security.Roles

trait Secured extends ProfileComponentImpl {
  private def username(request: RequestHeader) = request.session.get(SessionKeys.username)
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.ProfileController.login)

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user => 
    Action(request => f(user)(request))
  }

  def IsAuthenticated(role: Roles.Role, f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user => 
    profileService.profileIsInRole(user, role) match {
      case true => Action(request => f(user)(request))
      case false => Action(request => onUnauthorized(request))
    }
  }
}