package models

import data.ForumNews

case class NewsModel(newsItems: Seq[ForumNews])

object NewsModel {
  def apply(): NewsModel = NewsModel(Seq.empty[ForumNews])
}
