package data

trait PlayerRepositoryComponent {
  val playerRepository : PlayerRepository

  trait PlayerRepository {
    def getAllPlayers() : Seq[Player]
    def getPlayerByProfileId(profileId: Int) : Option[Player]
  }
}

trait PlayerRepositoryComponentImpl extends PlayerRepositoryComponent {
  val playerRepository = new PlayerRepositoryImpl

  class PlayerRepositoryImpl extends PlayerRepository {
    import anorm._ 
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current

    val singleRowParser = int(PlayerSchema.playerId) ~ str(PlayerSchema.name) ~ bool(PlayerSchema.isActive) ~ get[Option[Int]](TeamPlayerSchema.teamId) map flatten
    val multiRowParser = singleRowParser *
    val selectAllPlayersSql = 
      s"""
        SELECT 
          p.${PlayerSchema.playerId},
          p.${PlayerSchema.name},
          p.${PlayerSchema.isActive},
          tp.${TeamPlayerSchema.teamId}
        FROM ${PlayerSchema.tableName} AS p
          LEFT OUTER JOIN ${TeamPlayerSchema.tableName} AS tp ON p.${PlayerSchema.playerId} = tp.${TeamPlayerSchema.playerId}
      """

    def getAllPlayers() : Seq[Player] = DB.withConnection { implicit connection => 
      SQL(selectAllPlayersSql).as(multiRowParser).map(Player(_))
    }

    def getPlayerByProfileId(profileId: Int) : Option[Player] = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT 
            p.${PlayerSchema.playerId},
            p.${PlayerSchema.name},
            p.${PlayerSchema.isActive},
            NULL
          FROM ${PlayerSchema.tableName} AS p
            INNER JOIN ${PlayerProfileSchema.tableName} AS pp ON p.${PlayerSchema.playerId} = pp.${PlayerProfileSchema.playerId}
          WHERE ${PlayerProfileSchema.profileId} = {profileId}
        """
      )
      .on(
        'profileId -> profileId
      )
      .as(singleRowParser singleOpt)
      .map(Player(_))
    } 
  }
}
