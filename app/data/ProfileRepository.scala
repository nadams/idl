package data

import security._

trait ProfileRepositoryComponent {
  val profileRepository: ProfileRepository

  trait ProfileRepository {
    def getByUsername(username: String) : Option[Profile]
    def updateProfile(profile: Profile) : Boolean
    def getRolesForUsername(username: String) : Seq[Roles.Role]
    def insertProfile(profile: Profile) : Profile
    def addProfileToRole(profileId: Int, role: Roles.Role) : Boolean
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
      .as(profileParser singleOpt)
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
          SELECT pr.${RoleSchema.roleId}
          FROM ${ProfileSchema.tableName} AS p 
            INNER JOIN ${ProfileRoleSchema.tableName} AS pr ON p.${ProfileSchema.profileId} = pr.${ProfileRoleSchema.profileId} 
          WHERE p.${ProfileSchema.email} = {username}
          ORDER BY p.${ProfileSchema.profileId} ASC
        """
      )
      .on('username -> username)
      .as(scalar[Int] *)
      .map(Roles(_))
    }

    def insertProfile(profile: Profile) = DB.withConnection { implicit connection => 
      Profile(
        SQL(
          s"""
            INSERT INTO ${ProfileSchema.tableName} (
              ${ProfileSchema.email},
              ${ProfileSchema.displayName},
              ${ProfileSchema.password},
              ${ProfileSchema.dateCreated},
              ${ProfileSchema.passwordExpired},
              ${ProfileSchema.lastLoginDate}
            ) VALUES (
              {email},
              {displayName},
              {password},
              {dateCreated},
              {passwordExpired},
              {lastLoginDate}
            )
          """
        )
        .on(
          'email -> profile.email,
          'displayName -> profile.displayName,
          'password -> profile.password,
          'dateCreated -> profile.dateCreated,
          'passwordExpired -> profile.passwordExpired,
          'lastLoginDate -> profile.lastLoginDate
        )
        .executeInsert(scalar[Long] single).toInt,
        profile.email,
        profile.displayName,
        profile.password,
        profile.passwordExpired,
        profile.dateCreated,
        profile.lastLoginDate
      )
    }

    def addProfileToRole(profileId: Int, role: Roles.Role) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          INSERT INTO ${ProfileRoleSchema.tableName} (
            ${ProfileRoleSchema.profileId},
            ${ProfileRoleSchema.roleId}
          ) VALUES (
            {profileId},
            {roleId}
          )
        """
      )
      .on(
        'profileId -> profileId,
        'roleId -> role.id
      )
      .executeUpdate > 0
    }
  }
}
