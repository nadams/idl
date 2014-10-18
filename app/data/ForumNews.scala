package data

case class ForumNews(msgId: Int, subject: String, posterName: String, body: String)

object ForumNews {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectAllSql = 
    s"""
      SELECT 
        m.${ForumNewsSchema.msgId},
        m.${ForumNewsSchema.subject},
        m.${ForumNewsSchema.posterName},
        m.${ForumNewsSchema.body}
      FROM ${ForumNewsSchema.tableName} AS m
        INNER JOIN idlsmf_topics AS t ON m.id_msg = t.id_first_msg
      WHERE m.id_board = (SELECT b.id_board FROM idlsmf_boards AS b WHERE b.name = 'IDL News' LIMIT 1)
      ORDER BY m.poster_time DESC
      LIMIT {maxItems}
    """

  lazy val singleRowParser = 
    int(ForumNewsSchema.msgId) ~
    str(ForumNewsSchema.subject) ~
    str(ForumNewsSchema.posterName) ~
    str(ForumNewsSchema.body) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Int, String, String, String)) : ForumNews =
    ForumNews(x._1, x._2, x._3, x._4)
}
