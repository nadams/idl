package models.admin.seasons

import play.api.libs.json.Json
import data._

case class TeamSeasonsModel(seasons: Seq[TeamSeasonModel])
case class TeamSeasonModel(seasonId: Int, seasonName: String)

object TeamSeasonsModel {
  implicit val writesTeamSeasonModel = Json.writes[TeamSeasonModel]
  implicit val writesTeamSeasonsModel = Json.writes[TeamSeasonsModel]

  def toModel(seasons: Seq[Season]) = seasons.map { season => 
    TeamSeasonModel(season.seasonId, season.name)
  }
}
