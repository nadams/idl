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
    def getForumNews(maxItems: Int = 10) : Seq[ForumNews]
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

    def getAllNews() : Seq[News] = DB.withConnection { implicit connection => 
      SQL(News.selectAllSql)
      .as(News.multiRowParser)
      .map(News(_))
    }

    def getPagedNews(currentPage: Int, pageSize: Int) = DB.withConnection { implicit connection => 
      SQL(
        s"""
          ${News.selectAllSql}
          ORDER BY ${NewsSchema.dateCreated}
          LIMIT ${(currentPage - 1) * pageSize}, $pageSize
        """
      )
      .as(News.multiRowParser)
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
          ${News.selectAllSql}
          WHERE ${NewsSchema.newsId} = {newsId}
        """
      )
      .on('newsId -> id)
      .as(News.singleRowParser singleOpt)
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

    def getForumNews(maxItems: Int = 10) = DB.withConnection("forums") { implicit connection =>
      SQL(ForumNews.selectAllSql)
      .on('maxItems -> maxItems)
      .as(ForumNews.multiRowParser)
      .map(ForumNews(_))
    }
  }
}
