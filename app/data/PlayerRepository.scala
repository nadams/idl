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
    def getPlayers() : Seq[TeamPlayerRecord] 
    def getPlayersForProfile(profileId: Int) : Seq[Player]
    def removePlayerFromProfile(profileId: Int, playerId: Int) : Boolean
    def playerIsInAnyProfile(playerId: Int) : Boolean
    def addPlayerProfile(profileId: Int, playerId: Int, needsApproval: Boolean) : Option[Player] 
    def createPlayerAndAssignToProfile(profileId: Int, playerName: String) : Option[Player]
    def getPlayerProfile(profileId: Int, playerId: Int) : Option[PlayerProfile]
  }
}

trait PlayerRepositoryComponentImpl extends PlayerRepositoryComponent {
  val playerRepository = new PlayerRepositoryImpl

  class PlayerRepositoryImpl extends PlayerRepository {
    import anorm._ 
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current

    val singleRowParser = 
      int(PlayerSchema.playerId) ~ 
      str(PlayerSchema.playerName) ~ 
      bool(PlayerSchema.isActive) ~ 
      get[Option[Int]](TeamPlayerSchema.teamId) map flatten

    val multiRowParser = singleRowParser *

    val selectAllPlayersSql = 
      s"""
        SELECT 
          p.${PlayerSchema.playerId},
          p.${PlayerSchema.playerName},
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
            p.${PlayerSchema.playerName},
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
            p.${PlayerSchema.playerName},
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
            ${PlayerSchema.playerName},
            ${PlayerSchema.isActive}
          ) VALUES (
            {playerName},
            {isActive}
          )
        """
      )
      .on(
        'playerName -> player.playerName,
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
          WHERE p.${PlayerSchema.playerName} = {playerName}
        """
      ).on('playerName -> name)
      .as(singleRowParser singleOpt)
      .map(Player(_))
    }

    def getPlayerNamesThatExist(names: Set[String]) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT
            p.${PlayerSchema.playerName}
          FROM ${PlayerSchema.tableName} AS p
          WHERE p.${PlayerSchema.playerName} IN ({names})
        """
      ).on('names -> names.toSeq)
      .as(str(PlayerSchema.playerName) *)
      .toSet
    }

    def createPlayerFromName(name: String) = DB.withConnection { implicit connection =>
      insertPlayerFromName(name)
    }

    def batchCreatePlayerFromName(names: Set[String]) = DB.withTransaction { implicit connection => 
      names.map(insertPlayerFromName(_))
    }

    def getPlayers() = DB.withConnection { implicit connection =>
      SQL(TeamPlayerRecord.selectAll)
      .as(TeamPlayerRecord.multiRowParser)
      .map(TeamPlayerRecord(_))
    }

    def getPlayersForProfile(profileId: Int) = DB.withConnection { implicit connection => 
      SQL(Player.selectByProfileId)
      .on('profileId -> profileId)
      .as(Player.multiRowParser)
      .map(Player(_))
    }

    def removePlayerFromProfile(profileId: Int, playerId: Int) = DB.withConnection { implicit connection => 
      SQL(PlayerProfile.removePlayerFromProfileSql)
      .on('profileId -> profileId, 'playerId -> playerId)
      .executeUpdate > 0
    }

    def playerIsInAnyProfile(playerId: Int) = DB.withConnection { implicit connection => 
      SQL(PlayerProfile.playerIsInAnyProfileSql)
      .on('playerId -> playerId)
      .as(scalar[Long] single) > 0
    }

    def addPlayerProfile(profileId: Int, playerId: Int, needsApproval: Boolean) = DB.withConnection { implicit connection =>
      SQL(PlayerProfile.insertPlayerProfileSql)
      .on('profileId -> profileId, 'playerId -> playerId)
      .executeUpdate > 0

      getPlayer(playerId)
    }

    def createPlayerAndAssignToProfile(profileId: Int, playerName: String) = DB.withTransaction { implicit connection =>
      val playerId = SQL(Player.insertPlayer)
      .on('playerName -> playerName, 'isActive -> true)
      .executeInsert(scalar[Long] single)
      .toInt

      SQL(PlayerProfile.insertPlayerProfileSql)
      .on('playerId -> playerId, 'profileId -> profileId)
      .executeUpdate

      Some(Player(playerId, playerName, true))
    }

    def getPlayerProfile(profileId: Int, playerId: Int) = DB.withConnection { implicit connection =>
      SQL(PlayerProfile.selectByProfileIdAndPlayerId)
      .on('profileId -> profileId, 'playerId -> playerId)
      .as(PlayerProfile.singleRowParser singleOpt)
      .map(PlayerProfile(_))
    }

    private def insertPlayerFromName(name: String)(implicit connection: java.sql.Connection) : Player = 
      Player(
        SQL(
          s"""
            INSERT INTO ${PlayerSchema.tableName} (
              ${PlayerSchema.playerName},
              ${PlayerSchema.isActive}
            ) VALUES (
              {playerName},
              {isActive}
            )
          """
        ).on(
          'playerName -> name,
          'isActive -> true
        ).executeInsert(scalar[Long] single)
        .toInt,
        name,
        true,
        None
      )
  }
}
