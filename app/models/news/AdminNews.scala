package models.news

import com.github.nscala_time.time.Imports._

case class AdminNewsListItem(newsId: Int, subject: String, dateCreated: DateTime, dateModified: DateTime, postedByUsername: String)
case class AdminNews(newsItems: Seq[AdminNewsListItem])
