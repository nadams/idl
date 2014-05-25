package data

import org.joda.time.DateTime

case class Season(seasonId: Int, name: String, startDate: DateTime, endDate: DateTime) {
  def update(newName: String, newStartDate: DateTime, newEndDate: DateTime) =
    Season(seasonId, newName, newStartDate, newEndDate)
}

object Season {
  def apply(x: (Int, String, DateTime, DateTime)) : Season = 
    Season(x._1, x._2, x._3, x._4)
}
