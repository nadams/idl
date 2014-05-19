package models.admin.seasons

import org.joda.time.DateTime

case class Season(seasonId: Int, name: String, startDate: DateTime, endDate: DateTime)
case class Seasons(seasons: Seq[Season])

object Seasons {
  val empty = Seasons(Seq.empty[Season])
}
