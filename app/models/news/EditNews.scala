package models.news

case class EditNews(newsId: Int, subject: String, content: String) {
  val isNewNews = newsId == 0
}

object EditNews {
  def apply() : EditNews = EditNews(0, "", "")
}
