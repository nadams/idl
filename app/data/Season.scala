package data

import org.joda.time.DateTime

case class Season(seasonId: Int, name: String, startDate: DateTime, endDate: DateTime)

object Season {
  def apply(x: (Int, String, DateTime, DateTime)) : Season = 
    Season(x._1, x._2, x._3, x._4)
}
