package models.profile

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import components.ProfileComponentImpl

case class PasswordModel(currentPassword: String, newPassword: String, confirmPassword: String)

object PasswordModel {
  def apply(): PasswordModel = PasswordModel("", "", "")
}

case class PasswordModelErrors(
  currentPasswordError: String,
  newPasswordError: String,
  confirmPasswordError: String,
  globalErrors: Seq[String]
)

object PasswordModelErrors {
  implicit val writesPasswordModelErrors = Json.writes[PasswordModelErrors]
  lazy val empty = PasswordModelErrors("", "", "", Seq.empty[String])
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
      )(PasswordModel.apply)(PasswordModel.unapply)
      verifying("error.passwordsDoNotMatch", result => result.newPassword == result.confirmPassword)
    )
  }
}
