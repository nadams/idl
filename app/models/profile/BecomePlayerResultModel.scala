package models.profile

import play.api.libs.json.Json
import data._

case class BecomePlayerResultModel(playerResult: AddPlayerNameResultModel, resultMessage: String)

object BecomePlayerResultModel {
  implicit val writesBecomPlayerResultModel = Json.writes[BecomePlayerResultModel]

  def toModel(record: PlayerProfileRecord, resultMessage: String) =
    BecomePlayerResultModel(AddPlayerNameResultModel.toModel(record), resultMessage)
}
