package data

case class ForumNews(msgId: Long, topicId: Int, subject: String, posterName: String, body: String, posterTime: Long)

object ForumNews {
  import anorm._ 
  import anorm.SqlParser._
  import AnormExtensions._

  lazy val selectAllSql = 
    s"""
      SELECT 
        m.${ForumNewsSchema.msgId},
        t.${ForumNewsSchema.topicsTopicId},
        m.${ForumNewsSchema.subject},
        m.${ForumNewsSchema.posterName},
        m.${ForumNewsSchema.body},
        m.${ForumNewsSchema.posterTime}
      FROM ${ForumNewsSchema.tableName} AS m
        INNER JOIN ${ForumNewsSchema.topicsTableName} AS t ON m.${ForumNewsSchema.msgId} = t.${ForumNewsSchema.topicsFirstMsgId}
      WHERE m.${ForumNewsSchema.boardId} = (SELECT b.${ForumNewsSchema.boardBoardId} FROM ${ForumNewsSchema.boardsTableName} AS b WHERE b.${ForumNewsSchema.boardsName} = 'IDL News' LIMIT 1)
      ORDER BY m.${ForumNewsSchema.posterTime} DESC
      LIMIT {maxItems}
    """

  lazy val singleRowParser = 
    long(ForumNewsSchema.msgId) ~
    int(ForumNewsSchema.topicsTopicId) ~
    str(ForumNewsSchema.subject) ~
    str(ForumNewsSchema.posterName) ~
    str(ForumNewsSchema.body) ~
    long(ForumNewsSchema.posterTime) map flatten

  lazy val multiRowParser = singleRowParser *

  def apply(x: (Long, Int, String, String, String, Long)) : ForumNews =
    ForumNews(x._1, x._2, x._3, x._4, x._5, x._6)
}
