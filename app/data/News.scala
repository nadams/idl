package data

import com.github.nscala_time.time.Imports._

case class News(newsId: Int, subject: String, dateCreated: DateTime, dateModified: DateTime, content: String, postedByProfileId: Int)

object News {
  def apply(x: (Int, String, DateTime, DateTime, String, Int)) : News = 
    News(x._1, x._2, x._3, x._4, x._5, x._6)
}
