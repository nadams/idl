package data

case class RoundResult(roundResultId: Int, gameId: Int, roundId: Int, playerId: Int, captures: Int, pCaptures: Int, drops: Int, frags: Int, deaths: Int) {
  val fragPercentage : Double = if(frags > 0) deaths.toDouble / frags * 100.0 else 100.0
  val capturePercentage : Double = 0.0
  val pickupPercentage : Double = 0.0
  val stopPercentage : Double = 0.0
}

object RoundResult {
  lazy val removeRoundResult = 
    s"""
      DELETE FROM ${RoundResultSchema.tableName}
      WHERE ${RoundResultSchema.roundId} = {roundId}
    """

  def apply(x: (Int, Int, Int, Int, Int, Int, Int, Int, Int)) : RoundResult = 
    RoundResult(x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9)
}
