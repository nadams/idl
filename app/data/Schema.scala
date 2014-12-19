package data

object PlayerSchema {
  val tableName = "Player"
  val playerId = "PlayerId"
  val playerName = "PlayerName"
  val isActive = "IsActive"
  val dateCreated = "DateCreated"
}

object TeamSchema {
  val tableName = "Team"
  val teamId = "TeamId"
  val teamName = "TeamName"
  val isActive = "IsActive"
  val dateCreated = "DateCreated"
}

object TeamPlayerSchema {
  val tableName = "TeamPlayer"
  val teamId = "TeamId"
  val playerId = "PlayerId"
  val isCaptain = "IsCaptain"
  val isApproved = "IsApproved"
}

object TeamSeasonSchema {
  val tableName = "TeamSeason"
  val teamId = "TeamId"
  val seasonId = "SeasonId"
}

object ProfileSchema {
  val tableName = "Profile"
  val profileId = "ProfileId"
  val email = "Email"
  val displayName = "DisplayName"
  val password = "Password"
  val dateCreated = "DateCreated"
  val passwordExpired = "PasswordExpired"
  val lastLoginDate = "LastLoginDate"
}

object RoleSchema {
  val tableName = "Role"
  val roleId = "RoleId"
  val roleName = "RoleName"
}

object ProfileRoleSchema {
  val tableName = "ProfileRole"
  val roleId = "RoleId"
  val profileId = "ProfileId"
}

object SeasonSchema {
  val tableName = "Season"
  val seasonId = "SeasonId"
  val name = "Name"
  val startDate = "StartDate"
  val endDate = "EndDate"
}

object NewsSchema {
  val tableName = "News"
  val newsId = "NewsId"
  val subject = "Subject"
  val dateCreated = "DateCreated"
  val dateModified = "DateModified"
  val content = "Content"
  val postedByProfileId = "PostedByProfileId"
}

object PlayerProfileSchema {
  val tableName = "PlayerProfile"
  val playerId = "PlayerId"
  val profileId = "ProfileId"
  val isApproved = "IsApproved"
}

object GameSchema {
  val tableName = "Game"
  val gameId = "GameId"
  val weekId = "WeekId"
  val seasonId = "SeasonId"
  val gameTypeId = "GameTypeId"
  val scheduledPlayTime = "ScheduledPlayTime"
  val dateCompleted = "DateCompleted"
}

object TeamGameSchema {
  val tableName = "TeamGame"
  val gameId = "GameId"
  val team1Id = "Team1Id"
  val team2Id = "Team2Id"
}

object GameDemoSchema {
  val tableName = "GameDemo"
  val gameDemoId = "GameDemoId"
  val gameId = "GameId"
  val playerId = "PlayerId"
  val filename = "Filename"
  val dateUploaded = "DateUploaded"
  val demoFile = "DemoFile"
}

object WeekSchema {
  val tableName = "Week"
  val weekId = "WeekId"
  val name = "Name"
}

object RoundSchema {
  val tableName = "Round"
  val roundId = "RoundId"
  val gameId = "GameId"
  val mapNumber = "MapNumber"
  val isEnabled = "IsEnabled"
}

object RoundResultSchema {
  val tableName = "RoundResult"
  val roundResultId = "RoundResultId"
  val roundId = "RoundId"
  val playerId = "PlayerId"
  val captures = "Captures"
  val pCaptures = "PCaptures"
  val drops = "Drops"
  val frags = "Frags"
  val deaths = "Deaths"
}

object ForumNewsSchema {
  val tableName = "idlsmf_messages"
  val msgId = "id_msg"
  val boardId = "id_board"
  val subject = "subject"
  val posterName = "poster_name"
  val body = "body"
  val posterTime = "poster_time"
  val topicsTableName = "idlsmf_topics"
  val boardsTableName = "idlsmf_boards"
  val boardBoardId = "id_board"
  val boardsName = "name"
  val topicsTopicId = "id_topic"
  val topicsFirstMsgId = "id_first_msg"
}

object PasswordTokenSchema {
  val tableName = "PasswordToken"
  val passwordTokenId = "PasswordTokenId"
  val profileId = "ProfileId"
  val token = "Token"
  val dateCreated = "DateCreated"
  val isClaimed = "IsClaimed"
}
