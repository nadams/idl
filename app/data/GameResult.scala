package data

case class GameResult(gameResultId: Int, gameId: Int, playerId: Int, captures: Int, pCaptures: Int, drops: Int, frags: Int, deaths: Int) {
  def fragPercentage : Double = if(frags > 0) deaths.toDouble / frags * 100.0 else 100.0
  def capturePercentage : Double = 0.0
  def pickupPercentage : Double = 0.0
  def stopPercentage : Double = 0.0
}

object GameResult {
  def apply(x: (Int, Int, Int, Int, Int, Int, Int, Int)) : GameResult = 
    GameResult(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8)
}
