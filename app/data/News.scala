package data

import com.github.nscala_time.time.Imports._

case class News(newsId: Int, subject: String, dateCreated: DateTime, dateModified: DateTime, postedByProfile: Profile)