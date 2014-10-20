package models.profile

import play.api.libs.json.Json

case class IndexModel(profileId: Int, profileIsPlayer: Boolean)

object IndexModel {
  implicit val writesIndexModel = Json.writes[IndexModel] 
}

case class BecomePlayerResultModel(success: Boolean, message: String)

object BecomePlayerResultModel {
  implicit val writesBecomePlayerResultModel = Json.writes[BecomePlayerResultModel]
}
