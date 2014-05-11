package controllers

import play.api._
import play.api.mvc._
import models.ProfileModel
import components.ProfileComponentImpl
import _root_.data._

object ProfileController extends Controller with ProfileComponentImpl {
	def get = Action {
		val profile = profileService.getByUsername("arst@arst.com").getOrElse(Profile(0, "not found", "", false, null, null))

		Ok(views.html.profile.login(ProfileModel(profile.email, "")))
	}

	def post = Action { implicit request =>
		Redirect("/")
	}
}