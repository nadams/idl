package models.news

case class EditNews(newsId: Int)

object EditNews {
  def apply() : EditNews = EditNews(0)
}
