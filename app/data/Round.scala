package data

case class Round(roundId: Int, gameId: Int, mapNumber: String, isEnabled: Boolean)

object Round {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectRound = 
    s"""
      SELECT
        ${RoundSchema.roundId},
        ${RoundSchema.gameId},
        ${RoundSchema.mapNumber},
        ${RoundSchema.isEnabled}
      FROM ${RoundSchema.tableName}
      WHERE ${RoundSchema.roundId} = {roundId}
    """

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
      UPDATE ${RoundSchema.tableName}
      SET ${RoundSchema.isEnabled} = 0
      WHERE ${RoundSchema.roundId} = {roundId}
    """

  lazy val singleRowParser = 
    int(RoundSchema.roundId) ~
    int(RoundSchema.gameId) ~
    str(RoundSchema.mapNumber) ~
    bool(RoundSchema.isEnabled) map flatten

  def apply(x: (Int, Int, String, Boolean)) : Round = 
    Round(
      x._1,
      x._2,
      x._3,
      x._4
    )
}
