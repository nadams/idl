package data

trait PlayerRepositoryComponent {
  val playerRepository : PlayerRepository

  trait PlayerRepository {
    def getAllPlayers() : Seq[Player]
    def getPlayersByProfileId(profileId: Int) : Seq[Player]
    def getPlayer(playerId: Int) : Option[Player]
    def insertPlayerWithProfile(player: Player, profileId: Int) : Option[Int] 
    def insertPlayerProfile(playerId: Int, profileId: Int) : Boolean
    def getPlayerByName(name: String) : Option[Player]
    def getPlayerNamesThatExist(names: Set[String]) : Set[String]
    def createPlayerFromName(name: String) : Player
    def batchCreatePlayerFromName(names: Set[String]) : Set[Player]
    def getPlayers() : Seq[TeamPlayerRecord] 
    def removePlayerFromProfile(profileId: Int, playerId: Int) : Boolean
    def getNumberOfPlayersForProfile(playerId: Int) : Int
    def playerIsInAnyProfile(playerId: Int) : Boolean
    def addPlayerProfile(profileId: Int, playerId: Int, needsApproval: Boolean) : Option[Player] 
    def createPlayerAndAssignToProfile(profileId: Int, playerName: String) : Option[Player]
    def getPlayerProfile(profileId: Int, playerId: Int) : Option[PlayerProfile]
    def getPlayerProfileRecord(profileId: Int, playerId: Int) : Option[PlayerProfileRecord]
    def getPlayerProfileRecordsForProfile(profileId: Int) : Seq[PlayerProfileRecord] 
    def getFellowPlayersForProfile(profileId: Int) : Seq[FellowPlayerRecord]
  }
}

trait PlayerRepositoryComponentImpl extends PlayerRepositoryComponent {
  val playerRepository = new PlayerRepositoryImpl

  class PlayerRepositoryImpl extends PlayerRepository {
    import anorm._ 
    import anorm.SqlParser._
    import play.api.db.DB
    import play.api.Play.current
    import org.joda.time.{ DateTime, DateTimeZone }
    import AnormExtensions._

    def getAllPlayers() : Seq[Player] = DB.withConnection { implicit connection => 
      SQL(Player.selectAllPlayersSql)
      .as(Player.multiRowParser)
      .map(Player(_))
    }

    def getPlayersByProfileId(profileId: Int) : Seq[Player] = DB.withConnection { implicit connection => 
      SQL(Player.selectByProfileId)
      .on('profileId -> profileId)
      .as(Player.multiRowParser)
      .map(Player(_))
    } 

    def getPlayer(playerId: Int) : Option[Player] = DB.withConnection { implicit connection => 
      SQL(Player.selectPlayer)
      .on('playerId -> playerId)
      .as(Player.singleRowParser singleOpt)
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
      .on('playerName -> player.playerName, 'isActive -> player.isActive)
      .executeInsert(scalar[Long] single)
      .toInt

      val result = insertPlayerProfile(playerId, profileId)

      if(playerId > 0 && result) Some(playerId)
      else None
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
      SQL(Player.selectByPlayerName)
      .on('playerName -> name)
      .as(Player.singleRowParser singleOpt)
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
      .on('profileId -> profileId, 'playerId -> playerId, 'isApproved -> !needsApproval)
      .executeUpdate > 0

      getPlayer(playerId)
    }

    def createPlayerAndAssignToProfile(profileId: Int, playerName: String) = DB.withTransaction { implicit connection =>
      val now = new DateTime(DateTimeZone.UTC)

      val playerId = SQL(Player.insertPlayer)
      .on('playerName -> playerName, 'isActive -> true, 'dateCreated -> now)
      .executeInsert(scalar[Long] single)
      .toInt

      SQL(PlayerProfile.insertPlayerProfileSql)
      .on('playerId -> playerId, 'profileId -> profileId, 'isApproved -> true)
      .executeUpdate

      Some(Player(playerId, playerName, true, now))
    }

    def getPlayerProfile(profileId: Int, playerId: Int) = DB.withConnection { implicit connection =>
      SQL(PlayerProfile.selectByProfileIdAndPlayerId)
      .on('profileId -> profileId, 'playerId -> playerId)
      .as(PlayerProfile.singleRowParser singleOpt)
      .map(PlayerProfile(_))
    }

    def getPlayerProfileRecord(profileId: Int, playerId: Int) = DB.withConnection { implicit connection =>
      SQL(PlayerProfileRecord.selectByProfileIdAndPlayerId)
      .on('playerId -> playerId, 'profileId -> profileId)
      .as(PlayerProfileRecord.singleRowParser singleOpt)
      .map(PlayerProfileRecord(_))
    }
    
    def getPlayerProfileRecordsForProfile(profileId: Int) = DB.withConnection { implicit connection =>
      SQL(PlayerProfileRecord.selectByProfileId)
      .on('profileId -> profileId)
      .as(PlayerProfileRecord.multiRowParser)
      .map(PlayerProfileRecord(_))
    }

    def getNumberOfPlayersForProfile(profileId: Int) = DB.withConnection { implicit connection =>
      SQL(PlayerProfile.numberOfPlayersForProfileSql)
      .on('profileId -> profileId)
      .as(scalar[Long] single)
      .toInt
    }

    def getFellowPlayersForProfile(profileId: Int) = DB.withConnection { implicit connection =>
      SQL(FellowPlayerRecord.selectByPlayerId)
      .on('profileId -> profileId)
      .as(FellowPlayerRecord.multiRowParser)
      .map(FellowPlayerRecord(_))
    } 

    private def insertPlayerFromName(name: String)(implicit connection: java.sql.Connection) : Player = {
      val now = new DateTime(DateTimeZone.UTC)
      Player(
        SQL(Player.insertPlayer).on(
          'playerName -> name,
          'isActive -> true,
          'dateCreated -> now
        ).executeInsert(scalar[Long] single)
        .toInt,
        name,
        true,
        now
      )
    }
  }
}
