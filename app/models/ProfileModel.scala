package models

import play.api.data._
import play.api.data.Forms._
import components.ProfileComponentImpl

case class ProfileModel(username: String, password: String)

object ProfileModel {
  def apply() : ProfileModel = ProfileModel("", "")
}

object LoginForm extends ProfileComponentImpl {
  def apply() = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(ProfileModel.apply)(ProfileModel.unapply)
    verifying("Invalid username/password", result => result match {
      case ProfileModel(email, password) => profileService.authenticate(email, password)
    })
  )
}
