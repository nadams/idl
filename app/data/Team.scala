package data

case class Team(teamId: Int, name: String, isActive: Boolean)

object Team {
  def apply(x: (Int, String, Boolean)) : Team = 
    Team(x._1, x._2, x._3)
}