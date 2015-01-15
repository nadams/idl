package data

import org.joda.time.DateTime

case class Team(teamId: Int, teamName: String, isActive: Boolean, dateCreated: DateTime)

object Team {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectByName = 
    s"""
      SELECT
        ${TeamSchema.teamId},
        ${TeamSchema.teamName},
        ${TeamSchema.isActive},
        ${TeamSchema.dateCreated}
      FROM ${TeamSchema.tableName}
      WHERE ${TeamSchema.teamName} = {teamName}
    """

  val singleRowParser = 
    int(TeamSchema.teamId) ~ 
    str(TeamSchema.teamName) ~ 
    bool(TeamSchema.isActive) ~ 
    datetime(TeamSchema.dateCreated) map flatten
    
  val multiRowParser = singleRowParser *

  def apply(x: (Int, String, Boolean, DateTime)) : Team = 
    Team(x._1, x._2, x._3, x._4)
}
