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

  class ProfileRepositoryImpl extends ProfileRepository {
    import java.sql._
    import anorm._ 
    import anorm.SqlParser._
    import org.joda.time.DateTime
    import play.api.db.DB
    import play.api.Play.current
    import AnormExtensions._

    val profileParser = 
      int(ProfileSchema.profileId) ~ 
      str(ProfileSchema.email) ~ 
      str(ProfileSchema.displayName) ~ 
      str(ProfileSchema.password) ~ 
      bool(ProfileSchema.passwordExpired) ~ 
      get[DateTime](ProfileSchema.dateCreated) ~ 
      get[DateTime](ProfileSchema.lastLoginDate) map(flatten)

    def getByUsername(username: String) : Option[Profile] = DB.withConnection { implicit connection =>
      SQL(
        s"""
          SELECT *
          FROM ${ProfileSchema.tableName}
          WHERE ${ProfileSchema.email} = {email}
        """
      )
      .on('email -> username)
      .singleOpt(profileParser)
      .map(Profile(_))
    }

    def updateProfile(profile: Profile) : Boolean = DB.withConnection { implicit connection =>
      SQL(
        s"""
          UPDATE ${ProfileSchema.tableName}
          SET 
            ${ProfileSchema.email} = {email},
            ${ProfileSchema.displayName} = {displayName},
            ${ProfileSchema.password} = {password},
            ${ProfileSchema.dateCreated} = {dateCreated},
            ${ProfileSchema.passwordExpired} = {passwordExpired},
            ${ProfileSchema.lastLoginDate} = {lastLoginDate}
          WHERE ${ProfileSchema.profileId} = {profileId}
        """
      ).on(
        'profileId -> profile.profileId,
        'email -> profile.email,
        'displayName -> profile.displayName,
        'password -> profile.password,
        'dateCreated -> profile.dateCreated,
        'passwordExpired -> profile.passwordExpired,
        'lastLoginDate -> profile.lastLoginDate
      ).executeUpdate > 0
    }

    def getRolesForUsername(username: String) : Seq[Roles.Role] = DB.withConnection { implicit connection => 
      SQL(
        s"""
          SELECT 
            pr.${RoleSchema.roleId}
          FROM ${ProfileSchema.tableName} AS p 
            INNER JOIN ${ProfileRoleSchema.tableName} AS pr ON p.${ProfileSchema.profileId} = pr.${ProfileRoleSchema.profileId} 
          WHERE p.${ProfileSchema.email} = {username}
        """
      )
      .on('username -> username)
      .as(scalar[Int] *)
      .map(Roles(_))
    }
  }
}
