package controllers

import play.api._
import play.api.mvc._
import models.ProfileModel

object ProfileController extends Controller {
	def get = Action {
		Ok(views.html.profile.login(ProfileModel()))
	}

	def post = Action { implicit request =>
		Redirect("/")
	}
}