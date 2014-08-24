package data

trait PlayerRepositoryComponent {
  val playerRepository : PlayerRepository

  trait PlayerRepository {
    def getAllPlayers() : Seq[Player]
    def getPlayerByProfileId(profileId: Int) : Option[Player]
    def getPlayer(playerId: Int) : Option[Player]
    def insertPlayerWithProfile(player: Player, profileId: Int) : Boolean
    def insertPlayerProfile(playerId: Int, profileId: Int) : Boolean
    def getPlayerByName(name: String) : Option[Player]
    def getPlayerNamesThatExist(names: Set[String]) : Set[String]
    def createPlayerFromName(name: String) : Player
    def batchCreatePlayerFromName(names: Set[String]) : Set[Player]
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
            NULL AS ${TeamPlayerSchema.teamId}
          FROM ${PlayerSchema.tableName} AS p
            INNER JOIN ${PlayerProfileSchema.tableName} AS pp ON p.${PlayerSchema.playerId} = pp.${PlayerProfileSchema.playerId}
          WHERE ${PlayerProfileSchema.profileId} = {profileId}
        """
      )
      .on('profileId -> profileId)
      .as(singleRowParser singleOpt)
      .map(Player(_))
    } 

    def getPlayer(playerId: Int) : Option[Player] = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT 
            p.${PlayerSchema.playerId},
            p.${PlayerSchema.name},
            p.${PlayerSchema.isActive},
            NULL AS ${TeamPlayerSchema.teamId}
          FROM ${PlayerSchema.tableName} AS p
          WHERE ${PlayerSchema.playerId} = {playerId}
        """
      )
      .on('playerId -> playerId)
      .as(singleRowParser singleOpt)
      .map(Player(_))
    }

    def insertPlayerWithProfile(player: Player, profileId: Int) = DB.withConnection { implicit connection =>
      val playerId = SQL(
        s"""
          INSERT INTO ${PlayerSchema.tableName} (
            ${PlayerSchema.name},
            ${PlayerSchema.isActive}
          ) VALUES (
            {name},
            {isActive}
          )
        """
      )
      .on(
        'name -> player.name,
        'isActive -> player.isActive
      )
      .executeInsert(scalar[Long] single)
      .toInt

      val result = insertPlayerProfile(playerId, profileId)

      playerId > 0 && result
    }

    def insertPlayerProfile(playerId: Int, profileId: Int) = DB.withConnection { implicit connection =>
      SQL(
        s"""
          INSERT INTO ${PlayerProfileSchema.tableName} (
            ${PlayerProfileSchema.profileId},
            ${PlayerProfileSchema.playerId}
          ) VALUES (
            {profileId},
            {playerId}
          )
        """
      )
      .on(
        'profileId -> profileId,
        'playerId -> playerId
      )
      .executeUpdate > 0
    }

    def getPlayerByName(name: String) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllPlayersSql
          WHERE p.${PlayerSchema.name} = {name}
        """
      ).on('name -> name)
      .as(singleRowParser singleOpt)
      .map(Player(_))
    }

    def getPlayerNamesThatExist(names: Set[String]) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT
            p.${PlayerSchema.name}
          FROM ${PlayerSchema.tableName} AS p
          WHERE p.${PlayerSchema.name} IN ({names})
        """
      ).on('names -> names.toSeq)
      .as(str(PlayerSchema.name) *)
      .toSet
    }

    def createPlayerFromName(name: String) = DB.withConnection { implicit connection =>
      insertPlayerFromName(name)
    }

    def batchCreatePlayerFromName(names: Set[String]) = DB.withTransaction { implicit connection => 
      names.map(insertPlayerFromName(_))
    }

    private def insertPlayerFromName(name: String)(implicit connection: java.sql.Connection) : Player = 
      Player(
        SQL(
          s"""
            INSERT INTO ${PlayerSchema.tableName} (
              ${PlayerSchema.name},
              ${PlayerSchema.isActive}
            ) VALUES (
              {name},
              {isActive}
            )
          """
        ).on(
          'name -> name,
          'isActive -> true
        ).executeInsert(scalar[Long] single)
        .toInt,
        name,
        true,
        None
      )
  }
}
