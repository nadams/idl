package controllers

import play.api._
import play.api.mvc._
import components.ProfileComponentImpl
import models.{ ProfileModel, LoginForm }
import _root_.data._

object ProfileController extends Controller with ProfileComponentImpl {
	def get = Action {
		Ok(views.html.profile.login(ProfileModel()))
	}

	def post = Action { implicit request =>
		LoginForm().bindFromRequest.fold(
			errors => BadRequest(views.html.profile.login(ProfileModel(errors("username").value.getOrElse(""), ""))),
			user => Redirect(routes.Application.index).withSession(SessionKeys.username -> user.username)
		)
	}
}
