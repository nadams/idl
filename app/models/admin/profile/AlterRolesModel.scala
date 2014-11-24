package models.admin.profile

import play.api.libs.json.Json

case class AlterRolesModel(roleIds: Seq[Int])

object AlterRolesModel {
  implicit val formatsAlterRolesModel = Json.format[AlterRolesModel]
}
