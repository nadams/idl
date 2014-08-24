package controllers

import play.api._
import play.api.mvc._
import components.ProfileComponentImpl
import models.MenuModel
import security.Roles

trait ProvidesHeader extends ProfileComponentImpl {
  implicit def header[A](implicit request: Request[A]) : MenuModel = {
    val username = request.session.get(SessionKeys.username)
    val isInAdminRole = username.exists(u => profileService.profileIsInRole(u, Roles.Admin))

    MenuModel(username, isInAdminRole)
  }
}
