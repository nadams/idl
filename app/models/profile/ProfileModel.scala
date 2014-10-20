package models.profile

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import components.ProfileComponentImpl

case class ProfileModel(currentPassword: String, newPassword: String, confirmPassword: String)

object ProfileModel {
  def apply(): ProfileModel = ProfileModel("", "", "")
}

case class ProfileModelErrors(
  currentPasswordError: Option[String],
  newPasswordError: Option[String],
  confirmPasswordError: Option[String],
  globalErrors: Seq[String]
)

object ProfileModelErrors {
  def apply() : ProfileModelErrors = ProfileModelErrors(None, None, None, Seq.empty[String])
}

object ChangePasswordForm extends ProfileComponentImpl {
  def apply()(implicit request: RequestHeader) = {
    val username = request.session.get(controllers.SessionKeys.username).getOrElse("")
    val minLength = 6

    Form(
      mapping(
        "currentPassword" -> nonEmptyText(minLength = minLength)
          .verifying("Incorrect password", password => profileService.authenticate(username, password)),
        "newPassword" -> nonEmptyText(minLength = minLength),
        "confirmPassword" -> nonEmptyText(minLength = minLength)
      )(ProfileModel.apply)(ProfileModel.unapply)
      verifying("error.passwordsDoNotMatch", result => result.newPassword == result.confirmPassword)
    )
  }
}
