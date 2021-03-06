package data

case class ProfileSearchRecord(
  profileId: Int, 
  displayName: String, 
  email: String, 
  playerId: Option[Int],
  isApproved: Option[Boolean],
  playerName: Option[String])

object ProfileSearchRecord {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val searchByName = 
    s"""
      SELECT 
        p.${ProfileSchema.profileId},
        p.${ProfileSchema.email},
        p.${ProfileSchema.displayName},
        pp.${PlayerProfileSchema.playerId},
        pp.${PlayerProfileSchema.isApproved},
        p2.${PlayerSchema.playerName}
      FROM ${ProfileSchema.tableName} AS p 
        LEFT OUTER JOIN ${PlayerProfileSchema.tableName} AS pp ON p.${ProfileSchema.profileId} = pp.${PlayerProfileSchema.profileId}
        LEFT OUTER JOIN ${PlayerSchema.tableName} AS p2 ON pp.${PlayerProfileSchema.playerId} = p2.${PlayerSchema.playerId}
      WHERE p.${ProfileSchema.displayName} LIKE {name} 
        OR p.${ProfileSchema.email} LIKE {name}
        OR p2.${PlayerSchema.playerName} LIKE {name}
      LIMIT 25
    """

  lazy val singleRowParser = 
    int(ProfileSchema.profileId) ~
    str(ProfileSchema.displayName) ~
    str(ProfileSchema.email) ~
    get[Option[Int]](PlayerProfileSchema.playerId) ~
    get[Option[Boolean]](PlayerProfileSchema.isApproved) ~
    get[Option[String]](PlayerSchema.playerName) map flatten 

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, String, Option[Int], Option[Boolean], Option[String])) : ProfileSearchRecord = 
    ProfileSearchRecord(x._1, x._2, x._3, x._4, x._5, x._6)
}
