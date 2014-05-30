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
