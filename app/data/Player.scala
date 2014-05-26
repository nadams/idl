package data

case class Player(playerId: Int, name: String, isActive: Boolean, teamId: Option[Int])

object Player {
  def apply(x: (Int, String, Boolean, Option[Int])) : Player = Player(x._1, x._2, x._3, x._4)
}
