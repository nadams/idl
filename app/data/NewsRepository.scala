package data

trait NewsRepositoryComponent {
  val newsRepository: NewsRepository

  trait NewsRepository {
    def getAllNews() : Seq[News]
    def getPagedNews(currentPage: Int, pageSize: Int) : Seq[News]
    def removeNewsItem(id: Int) : Boolean
    def insertNews(news: News) : Boolean
    def getNewsById(id: Int) : Option[News]
    def updateNews(news: News) : Boolean
  }
}

trait NewsRepositoryComponentImpl extends NewsRepositoryComponent {
  val newsRepository: NewsRepository = new NewsRepositoryImpl

  class NewsRepositoryImpl extends NewsRepository {
    import java.sql._
    import anorm._ 
    import anorm.SqlParser._
    import org.joda.time.{ DateTime, DateTimeZone }
    import play.api.db.DB
    import play.api.Play.current
    import AnormExtensions._

    val newsParser = 
      int(NewsSchema.newsId) ~ 
      str(NewsSchema.subject) ~ 
      get[DateTime](NewsSchema.dateCreated) ~ 
      get[DateTime](NewsSchema.dateModified) ~ 
      str(NewsSchema.content) ~ 
      int(NewsSchema.postedByProfileId) map(flatten)

    val multiRowNewsParser = newsParser *
    val selectAllNewsSql = 
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

    def getAllNews() : Seq[News] = DB.withConnection { implicit connection => 
      SQL(selectAllNewsSql)
      .as(multiRowNewsParser)
      .map(News(_))
    }

    def getPagedNews(currentPage: Int, pageSize: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllNewsSql
          ORDER BY ${NewsSchema.dateCreated}
          LIMIT ${(currentPage - 1) * pageSize}, $pageSize
        """
      )
      .as(multiRowNewsParser)
      .map(News(_))
    }

    def removeNewsItem(id: Int) : Boolean = DB.withConnection { implicit connection => 
      SQL(
        s"""
          DELETE FROM ${NewsSchema.tableName}
          WHERE ${NewsSchema.newsId} = {newsId}
        """
      )
      .on('newsId -> id)
      .executeUpdate > 0
    }

    def insertNews(news: News) : Boolean = DB.withConnection { implicit connection => 
      import play.Logger

      SQL(
        s"""
          INSERT INTO ${NewsSchema.tableName} (
            ${NewsSchema.subject}, 
            ${NewsSchema.dateCreated}, 
            ${NewsSchema.dateModified}, 
            ${NewsSchema.content}, 
            ${NewsSchema.postedByProfileId}
          ) VALUES (
            {subject}, 
            {dateCreated}, 
            {dateModified}, 
            {content}, 
            {postedByProfileId}
          )
        """
      )
      .on(
        'subject -> news.subject,
        'dateCreated -> news.dateCreated,
        'dateModified -> news.dateModified,
        'content -> news.content,
        'postedByProfileId -> news.postedByProfileId
      )
      .executeInsert(scalar[Long] single) > 0
    }

    def getNewsById(id: Int) : Option[News] = DB.withConnection { implicit connection => 
      SQL(
        s"""
          $selectAllNewsSql
          WHERE ${NewsSchema.newsId} = {newsId}
        """
      )
      .on('newsId -> id)
      .as(newsParser singleOpt)
      .map(News(_))
    }

    def updateNews(news: News) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          UPDATE ${NewsSchema.tableName}
          SET
            ${NewsSchema.subject} = {subject},
            ${NewsSchema.dateCreated} = {dateCreated},
            ${NewsSchema.dateModified} = {dateModified},
            ${NewsSchema.content} = {content},
            ${NewsSchema.postedByProfileId} = {postedByProfileId}
          WHERE ${NewsSchema.newsId} = {newsId}
        """
      )
      .on(
        'newsId -> news.newsId,
        'subject -> news.subject,
        'dateCreated -> news.dateCreated,
        'dateModified -> news.dateModified,
        'content -> news.content,
        'postedByProfileId -> news.postedByProfileId
      )
      .executeUpdate > 0
    }
  }
}
