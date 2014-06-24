package models.admin.teams

import play.api.libs.json._

case class TeamPlayersModel(seasons: Seq[SeasonModel], urls: Map[String, String])
case class SeasonModel(seasonId: Int, seasonName: String)

object TeamPlayersModel {
  implicit val writesSeasonModel = Json.writes[SeasonModel]
  implicit val writesIndexModel = Json.writes[TeamPlayersModel]

  val empty =  TeamPlayersModel(Seq.empty[SeasonModel], Map.empty[String, String])
}
