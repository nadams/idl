package models

import play.api.libs.json.Json
import data.ForumNews

case class NewsModel(newsItems: Seq[ForumNewsModel])
case class ForumNewsModel(msdId: Long, topicId: Int, subject: String, body: String, posterName: String)

object NewsModel {
  implicit val writesForumNewsModel = Json.writes[ForumNewsModel]
  implicit val writesNewsModel = Json.writes[NewsModel]

  def toModel(data: Seq[ForumNews]) =
    NewsModel(data.map(ForumNewsModel.toModel(_)))
}

object ForumNewsModel {
  def toModel(data: ForumNews) = 
    ForumNewsModel(data.msgId, data.topicId, data.subject, data.body, data.posterName)
}
