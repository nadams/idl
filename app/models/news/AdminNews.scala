package models.news

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.github.nscala_time.time.Imports._
import _root_.data.News

case class AdminNews(newsItems: Seq[AdminNewsListItem])
case class AdminNewsListItem(newsId: Int, subject: String, dateCreated: DateTime, dateModified: DateTime, postedByUsername: String, editUrl: String, removeUrl: String)

object AdminNews {
  implicit val adminNewsListItemWrites = Json.writes[AdminNewsListItem]
  implicit val adminNewsWrites = Json.writes[AdminNews]

  def toModels(newsItems: Seq[News], username: String, routes: controllers.ReverseNewsController) = 
    AdminNews(
      newsItems.map(
        x => AdminNewsListItem(
          x.newsId, 
          x.subject, 
          x.dateCreated, 
          x.dateModified, 
          username,
          routes.edit(x.newsId).url,
          routes.remove(x.newsId).url
        )
      )
    ) 
}
