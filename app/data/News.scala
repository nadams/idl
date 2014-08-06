package data

import com.github.nscala_time.time.Imports._

case class News(newsId: Int, subject: String, dateCreated: DateTime, dateModified: DateTime, content: String, postedByProfileId: Int) {
  
  def updateContent(newSubject: String, newContent: String) =
    News(newsId, newSubject, dateCreated, new DateTime(DateTimeZone.UTC), newContent, postedByProfileId)
}

object News {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  val selectAllSql = 
    s"""
      SELECT 
        ${NewsSchema.newsId},
        ${NewsSchema.subject},
        ${NewsSchema.dateCreated},
        ${NewsSchema.dateModified},
        ${NewsSchema.content},
        ${NewsSchema.postedByProfileId}
      FROM ${NewsSchema.tableName}
    """

  val singleRowParser = 
    int(NewsSchema.newsId) ~ 
    str(NewsSchema.subject) ~ 
    datetime(NewsSchema.dateCreated) ~ 
    datetime(NewsSchema.dateModified) ~ 
    str(NewsSchema.content) ~ 
    int(NewsSchema.postedByProfileId) map(flatten)

  val multiRowParser = singleRowParser *

  def apply(x: (Int, String, DateTime, DateTime, String, Int)) : News = 
    News(x._1, x._2, x._3, x._4, x._5, x._6)
}
