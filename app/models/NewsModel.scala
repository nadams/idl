package models

import data.News

case class NewsModel(newsItems: Seq[News])

object NewsModel {
  def apply(): NewsModel = NewsModel(Seq.empty[News])
}