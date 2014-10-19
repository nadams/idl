package data

case class ForumNews(msgId: Long, subject: String, posterName: String, body: String)

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
        INNER JOIN ${ForumNewsSchema.topicsTableName} AS t ON m.${ForumNewsSchema.msgId} = t.${ForumNewsSchema.topicsFirstMsgId}
      WHERE m.${ForumNewsSchema.boardId} = (SELECT b.${ForumNewsSchema.boardBoardId} FROM ${ForumNewsSchema.boardsTableName} AS b WHERE b.${ForumNewsSchema.boardsName} = 'IDL News' LIMIT 1)
      ORDER BY m.${ForumNewsSchema.posterTime} DESC
      LIMIT {maxItems}
    """

  lazy val singleRowParser = 
    long(ForumNewsSchema.msgId) ~
    str(ForumNewsSchema.subject) ~
    str(ForumNewsSchema.posterName) ~
    str(ForumNewsSchema.body) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Long, String, String, String)) : ForumNews =
    ForumNews(x._1, x._2, x._3, x._4)
}
