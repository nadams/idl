package models.admin.profile

import play.api.libs.json.Json
import _root_.data.Profile

case class UpdateProfileModel(
  displayName: String,
  email: String,
  passwordExpired: Boolean
)

object UpdateProfileModel {
  implicit val formatsUpdateProfileModel = Json.format[UpdateProfileModel]

  def toModel(profile: Profile) = UpdateProfileModel(
    profile.displayName,
    profile.email,
    profile.passwordExpired
  )
}

