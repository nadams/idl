package models.admin.profile

import play.api.libs.json.Json

case class AlterPlayerModel(playerId: Int)

object AlterPlayerModel {
  implicit val formatsAlterPlayerModel = Json.format[AlterPlayerModel]
}

