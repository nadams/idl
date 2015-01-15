package models.profile

import play.api.data._
import play.api.data.Forms._
import components.ProfileComponentImpl

case class LoginModel(username: String, password: String)

object LoginModel {
  def apply() : LoginModel = LoginModel("", "")
}

object LoginForm extends ProfileComponentImpl {
  def apply() = Form(
    mapping(
      "username" -> text,
      "password" -> text
    )(LoginModel.apply)(LoginModel.unapply)
    verifying("Invalid username/password", result => result match {
      case LoginModel(email, password) => {
        val result = profileService.authenticate(email, password)
        if(result) profileService.updateLastLoginDate(email)
        result
      }
    })
  )
}
