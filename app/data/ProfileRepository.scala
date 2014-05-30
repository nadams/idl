package data

import security._

trait ProfileRepositoryComponent {
  val profileRepository: ProfileRepository

  trait ProfileRepository {
    def getByUsername(username: String) : Option[Profile]
    def updateProfile(profile: Profile) : Boolean
    def getRolesForUsername(username: String) : Seq[Roles.Role]
  }
}

trait ProfileRepositoryComponentImpl extends ProfileRepositoryComponent {
  val profileRepository : ProfileRepository = new ProfileRepositoryImpl

  trait ProfileSchema {
    val tableName = "Profile"
    val profileId = "ProfileId"
    val email = "Email"
    val displayName = "DisplayName"
    val password = "Password"
    val dateCreated = "DateCreated"
    val passwordExpired = "PasswordExpired"
    val lastLoginDate = "LastLoginDate"
  }

  class ProfileRepositoryImpl extends ProfileRepository with ProfileSchema {
    import java.sql._
    import anorm._ 
    import anorm.SqlParser._
    import org.joda.time.DateTime
    import play.api.db.DB
    import play.api.Play.current
    import AnormExtensions._

    val profileParser = int(profileId) ~ str(email) ~ str(displayName) ~ str(password) ~ bool(passwordExpired) ~ get[DateTime](dateCreated) ~ get[DateTime](lastLoginDate) map(flatten)

    def getByUsername(username: String) : Option[Profile] = DB.withConnection { implicit connection =>
      SQL(
        s"""
          SELECT *
          FROM $tableName
          WHERE $email = {email}
        """
      )
      .on('email -> username)
      .singleOpt(profileParser)
      .map(Profile(_))
    }

    def updateProfile(profile: Profile) : Boolean = DB.withConnection { implicit connection =>
      SQL(
        s"""
          UPDATE $tableName
          SET 
            $email = {email},
            $displayName = {displayName},
            $password = {password},
            $dateCreated = {dateCreated},
            $passwordExpired = {passwordExpired},
            $lastLoginDate = {lastLoginDate}
          WHERE $profileId = {profileId}
        """
      ).on(
        'profileId -> profile.profileId,
        'email -> profile.email,
        'displayName -> profile.displayName,
        'password -> profile.password,
        'dateCreated -> profile.dateCreated,
        'passwordExpired -> profile.passwordExpired,
        'lastLoginDate -> profile.lastLoginDate
      ).executeUpdate() > 0
    }

    def getRolesForUsername(username: String) : Seq[Roles.Role] = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT 
            pr.RoleId 
          FROM Profile AS p 
            INNER JOIN ProfileRole AS pr ON p.ProfileId = pr.ProfileId 
          WHERE p.Email = {username}
        """
      )
      .on(
        'username -> username
      )
      .as(scalar[Int] *)
      .map(Roles(_))
    }
  }
}
