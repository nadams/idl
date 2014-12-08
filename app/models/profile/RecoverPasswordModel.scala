package models.profile

import play.api.data._
import play.api.data.Forms._

case class ForgotPasswordModel(errors: Seq[String])

object ForgotPasswordModel {
  val empty = ForgotPasswordModel.toModel(Seq.empty[String])
  def toModel(errors: Seq[String]) = ForgotPasswordModel(errors)
}

case class RecoverPasswordModel(username: String)

object RecoverPasswordForm {
  def apply() = Form(
    mapping("username" -> nonEmptyText)(RecoverPasswordModel.apply)(RecoverPasswordModel.unapply)
  )
}
