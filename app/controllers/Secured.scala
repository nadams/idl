package controllers

import play.api._
import play.api.mvc._
import play.api.data._

trait Secured {
  private def username(request: RequestHeader) = request.session.get(SessionKeys.username)
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.ProfileController.login)

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user => 
    Action(request => f(user)(request))
  }
}