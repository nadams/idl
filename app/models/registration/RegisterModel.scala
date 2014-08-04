package models.registration

import play.api._
import play.api.data._
import play.api.data.Forms._
import org.joda.time.{ DateTime, DateTimeZone }
import _root_.data._
import models.FieldExtensions._
import models.FormExtensions._

case class RegisterModel(email: String, password: String, confirmPassword: String)

object RegisterModel {
  val empty = RegisterModel("", "", "")

  def toEntity(model: RegisterModel) : Profile = {
    val now = new DateTime(DateTimeZone.UTC)

    Profile(
      0,
      model.email,
      model.email,
      model.password,
      false,
      now,
      now
    )
  }

  def toModel(errors: Form[RegisterModel]) = {
    val emailError = errors("email").formattedMessage

    RegisterModel(emailError._1, "", "")
  }
}

object RegisterModelForm extends components.ProfileComponentImpl {
  def apply() = Form(
    mapping(
      "email" -> email.verifying("error.emailExists", !profileService.getByUsername(_).isDefined),
      "password" -> nonEmptyText(minLength = 6),
      "passwordConfirm" -> nonEmptyText(minLength = 6)
    )(RegisterModel.apply)(RegisterModel.unapply)
    verifying("error.passwordsDoNotMatch" , result => result match {
      case RegisterModel(email, password, passwordConfirm) => password == passwordConfirm
    })
  )
}

case class RegisterModelErrors(
  emailError: Option[String], 
  passwordError: Option[String], 
  passwordConfirmError: Option[String], 
  globalErrors: Seq[String]
)

object RegisterModelErrors {
  val empty = RegisterModelErrors(None, None, None, Seq.empty[String])

  def toModel(errors: Form[RegisterModel]) = {
    val emailError = errors("email").formattedMessage
    val passwordError = errors("password").formattedMessage
    val passwordConfirmError = errors("passwordConfirm").formattedMessage
    val globalErrors = errors.formattedMessages

    RegisterModelErrors(emailError._2, passwordError._2, passwordConfirmError._2, globalErrors)
  }
}
