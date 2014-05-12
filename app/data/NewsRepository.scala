package data

trait NewsRepositoryComponent {
  val newsRepository: NewsRepository

  trait NewsRepository {
    def getAllNews() : Seq[News]
    def getPagedNews(currentPage: Int = 1, pageSize: Int = 15) : Seq[News]
  }
}

trait NewsSchema {
  val tableName = "News"
  val newsId = "newsId"
  val subject = "subject"
  val dateCreated = "dateCreated"
  val dateModified = "dateModified"
  val content = "content"
  val postedByProfileId = "postedByProfileId"
}

trait NewsRepositoryComponentImpl extends NewsRepositoryComponent {
  val newsRepository: NewsRepository = new NewsRepositoryImpl

  class NewsRepositoryImpl extends NewsRepository with NewsSchema {
    import java.sql._
    import anorm._ 
    import anorm.SqlParser._
    import org.joda.time.DateTime
    import play.api.db.DB
    import play.api.Play.current
    import AnormExtensions._

    val newsParser = int(newsId) ~ str(subject) ~ get[DateTime](dateCreated) ~ get[DateTime](dateModified) ~ str(content) ~ int(postedByProfileId) map(flatten)
    val multiRowNewsParser = newsParser *
    val selectAllNewsSql = 
      s"""
        SELECT 
          $newsId,
          $subject,
          $dateCreated,
          $dateModified,
          $content,
          $postedByProfileId
        FROM $tableName
      """ 

    override def getAllNews() : Seq[News] = DB.withConnection { implicit connection => 
      SQL(selectAllNewsSql)
      .as(multiRowNewsParser)
      .map(News(_))
    }

    override def getPagedNews(currentPage: Int = 1, pageSize: Int = 15) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllNewsSql
          ORDER BY $dateCreated
          LIMIT ${currentPage * pageSize}, $pageSize
        """
      )
      .as(multiRowNewsParser)
      .map(News(_))
    }
  }
}
