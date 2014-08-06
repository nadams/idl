package data

object PlayerSchema {
  val tableName = "Player"
  val playerId = "PlayerId"
  val name = "Name"
  val isActive = "IsActive"
}

object TeamSchema {
  val tableName = "Team"
  val teamId = "TeamId"
  val name = "Name"
  val isActive = "IsActive"
  val dateCreated = "DateCreated"
}

object TeamPlayerSchema {
  val tableName = "TeamPlayer"
  val teamId = "TeamId"
  val playerId = "PlayerId"
  val isCaptain = "IsCaptain"
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
}

object GameSchema {
  val tableName = "Game"
  val gameId = "GameId"
  val weekId = "WeekId"
  val seasonId = "SeasonId"
  val scheduledPlayTime = "ScheduledPlayTime"
  val dateCompleted = "DateCompleted"
}

object TeamGameSchema {
  val tableName = "TeamGame"
  val gameId = "GameId"
  val team1Id = "Team1Id"
  val team2Id = "Team2Id"
}

object GameResultSchema {
  val tableName = "GameResult"
  val gameResultId = "GameResultId"
  val gameId = "GameId"
  val playerId = "PlayerId"
  val captures = "Captures"
  val pCaptures = "PCaptures"
  val drops = "Drops"
  val frags = "Frags"
  val deaths = "Deaths"
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
