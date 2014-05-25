package models.admin.seasons

import play.api.libs.json._
import org.joda.time.DateTime
import formatters.DateTimeFormatter._
import data.Season

case class SeasonModel(seasonId: Int, name: String, startDate: DateTime, endDate: DateTime, editLink: String, removeLink: String)

object SeasonModel {
  implicit val writesSeason = Json.writes[SeasonModel]

  def toModels(seasons: Seq[Season], routes: controllers.ReverseSeasonController) = seasons.map { season => 
    SeasonModel(
      season.seasonId,
      season.name,
      season.startDate,
      season.endDate,
      routes.edit(season.seasonId).url,
      routes.remove(season.seasonId).url
    )
  }
}
