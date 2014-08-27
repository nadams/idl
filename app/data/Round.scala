package data

case class Round(roundId: Int, gameId: Int, mapName: String)

object Round {
  lazy val insertRound = 
    s"""
      INSERT INTO ${RoundSchema.tableName} (
        ${RoundSchema.gameId},
        ${RoundSchema.mapName}
      ) VALUES (
        {gameId},
        {mapName}
      )
    """

}