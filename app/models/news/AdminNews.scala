package models.news

import com.github.nscala_time.time.Imports._
import _root_.data.News

case class AdminNewsListItem(newsId: Int, subject: String, dateCreated: DateTime, dateModified: DateTime, postedByUsername: String)
case class AdminNews(newsItems: Seq[AdminNewsListItem])

object AdminNews {
  def toModels(newsItems: Seq[News]) = 
    AdminNews(newsItems.map(x => AdminNewsListItem(x.newsId, x.subject, x.dateCreated, x.dateModified, ""))) 
}
