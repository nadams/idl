package models.news

case class EditNews(newsId: Int) {
  val isNewNews = newsId == 0
}

object EditNews {
  def apply() : EditNews = EditNews(0)
}
