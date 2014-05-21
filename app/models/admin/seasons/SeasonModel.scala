package models.admin.seasons

import play.api.libs.json._
import org.joda.time.DateTime

case class Season(seasonId: Int, name: String, startDate: DateTime, endDate: DateTime)
case class Seasons(seasons: Seq[Season])

object Seasons {
  implicit val writesSeason = Json.writes[Season]
  implicit val writesSeasons = Json.writes[Seasons]

  val empty = Seasons(Seq.empty[Season])
}
