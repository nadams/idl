package data

import com.github.nscala_time.time.Imports._

case class Profile(profileId: Int, email: String, displayName: String, password: String, passwordExpired: Boolean, dateCreated: DateTime, lastLoginDate: DateTime)

object Profile {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectAll =
    s"""
      SELECT
        ${ProfileSchema.profileId},
        ${ProfileSchema.email},
        ${ProfileSchema.displayName},
        ${ProfileSchema.password},
        ${ProfileSchema.passwordExpired},
        ${ProfileSchema.dateCreated},
        ${ProfileSchema.lastLoginDate}
      FROM ${ProfileSchema.tableName}
    """

  lazy val selectByUsername =
    s"""
      $selectAll
      WHERE ${ProfileSchema.email} = {email}
    """

  lazy val selectById =
    s"""
      $selectAll
      WHERE ${ProfileSchema.profileId} = {profileId}
    """

  lazy val update =
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

  lazy val insert =
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

  lazy val singleRowParser  = 
    int(ProfileSchema.profileId) ~ 
    str(ProfileSchema.email) ~ 
    str(ProfileSchema.displayName) ~ 
    str(ProfileSchema.password) ~ 
    bool(ProfileSchema.passwordExpired) ~ 
    datetime(ProfileSchema.dateCreated) ~ 
    datetime(ProfileSchema.lastLoginDate) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, String, String, Boolean, DateTime, DateTime)) : Profile =
    Profile(x._1, x._2, x._3, x._4, x._5, x._6, x._7)
}
