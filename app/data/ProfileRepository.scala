package data

import security._

trait ProfileRepositoryComponent {
  val profileRepository: ProfileRepository

  trait ProfileRepository {
    def getByUsername(username: String) : Option[Profile]
    def getById(profileId: Int) : Option[Profile]
    def updateProfile(profile: Profile) : Boolean
    def getRolesForUsername(username: String) : Seq[Roles.Role]
    def insertProfile(profile: Profile) : Profile
    def addProfileToRole(profileId: Int, role: Roles.Role) : Boolean
    def searchProfiles(name: String) : Seq[ProfileSearchRecord]
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

    def getByUsername(username: String) : Option[Profile] = DB.withConnection { implicit connection =>
      SQL(Profile.selectByUsername)
      .on('email -> username)
      .as(Profile.singleRowParser singleOpt)
      .map(Profile(_))
    }

    def getById(profileId: Int) = DB.withConnection { implicit connection => 
      SQL(Profile.selectById)
      .on('profileId -> profileId)
      .as(Profile.singleRowParser singleOpt)
      .map(Profile(_))
    }

    def updateProfile(profile: Profile) : Boolean = DB.withConnection { implicit connection =>
      SQL(Profile.update)
      .on(
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
        SQL(Profile.insert)
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

    def searchProfiles(name: String) = DB.withConnection { implicit connection => 
      SQL(ProfileSearchRecord.searchByName)
      .on('name -> ("%" + name + "%"))
      .as(ProfileSearchRecord.multiRowParser)
      .map(ProfileSearchRecord(_))
    }
  }
}
