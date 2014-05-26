package data

trait PlayerRepositoryComponent {
  val playerRepository : PlayerRepository

  trait PlayerRepository {
    def getAllPlayers() : Seq[Player]
  }
}

trait PlayerRepositoryComponentImpl extends PlayerRepositoryComponent {
  val playerRepository = new PlayerRepositoryImpl

  trait PlayerSchema {
    val tableName = "Player"
    val playerId = "PlayerId"
    val name = "Name"
    val isActive = "IsActive"

    val teamPlayerTableName = "TeamPlayer"
    val teamPlayerTeamId = "TeamId"
    val teamPlayerPlayerId = "PlayerId"
  }

  class PlayerRepositoryImpl extends PlayerRepository with PlayerSchema {
    import anorm._ 
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current

    val singleRowParser = int(playerId) ~ str(name) ~ bool(isActive) ~ get[Option[Int]](teamPlayerTeamId) map flatten
    val multiRowParser = singleRowParser *
    val selectAllPlayersSql = 
      s"""
        SELECT 
          p.$playerId,
          p.$name,
          p.$isActive,
          tp.$teamPlayerTeamId
        FROM $tableName AS p
          LEFT OUTER JOIN $teamPlayerTableName AS tp ON p.$playerId = tp.$teamPlayerPlayerId
      """

    def getAllPlayers() : Seq[Player] = DB.withConnection { implicit connection => 
      SQL(selectAllPlayersSql).as(multiRowParser).map(Player(_))
    } 
  }
}
