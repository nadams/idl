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