package data

case class Round(roundId: Int, gameId: Int, mapNumber: String)

object Round {
  lazy val insertRound = 
    s"""
      INSERT INTO ${RoundSchema.tableName} (
        ${RoundSchema.gameId},
        ${RoundSchema.mapNumber}
      ) VALUES (
        {gameId},
        {mapNumber}
      )
    """

  lazy val removeRound =
    s"""
      DELETE FROM ${RoundSchema.tableName}
      WHERE ${RoundSchema.roundId} = {roundId}
    """
}
