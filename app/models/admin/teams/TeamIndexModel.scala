package models.admin.teams

import play.api.libs.json._

case class TeamIndexModel(seasons: Seq[SeasonModel])
case class SeasonModel(seasonId: Int, seasonName: String)

object TeamIndexModel {
  implicit val writesSeasonModel = Json.writes[SeasonModel]
  implicit val writesIndexModel = Json.writes[TeamIndexModel]

  val empty =  TeamIndexModel(Seq.empty[SeasonModel])
}
