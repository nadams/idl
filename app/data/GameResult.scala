package data

case class GameResult(gameResultId: Int, gameId: Int, playerId: Int, captures: Int, pCaptures: Int, drops: Int, frags: Int, deaths: Int) {
  val fragPercentage : Double = if(frags > 0) deaths.toDouble / frags * 100.0 else 100.0
  val capturePercentage : Double = 0.0
  val pickupPercentage : Double = 0.0
  val stopPercentage : Double = 0.0
}

object GameResult {
  def apply(x: (Int, Int, Int, Int, Int, Int, Int, Int)) : GameResult = 
    GameResult(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8)
}


