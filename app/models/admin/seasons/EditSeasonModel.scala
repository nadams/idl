package models.admin.seasons

import org.joda.time.{ DateTime, DateTimeZone }

case class EditSeasonModel(seasonId: Int, name: String, startDate: DateTime, endDate: DateTime) {
  val isNewSeason = seasonId == 0
}

object EditSeasonModel {
  lazy val now = new DateTime(DateTimeZone.UTC)
  lazy val empty = EditSeasonModel(0, "", now, now)
}

case class EditSeasonModelErrors(nameError: Option[String], startDateError: Option[DateTime], endDateError: Option[DateTime])

object EditSeasonModelErrors {
  val empty = EditSeasonModelErrors(None, None, None)
}
