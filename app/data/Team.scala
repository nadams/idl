package data

import org.joda.time.DateTime

case class Team(teamId: Int, teamName: String, isActive: Boolean, dateCreated: DateTime)

object Team {
  def apply(x: (Int, String, Boolean, DateTime)) : Team = 
    Team(x._1, x._2, x._3, x._4)
}
