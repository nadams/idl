package data

import com.github.nscala_time.time.Imports._

case class Profile(profileId: Int, email: String, displayName: String, password: String, passwordExpired: Boolean, dateCreated: DateTime, lastLoginDate: DateTime)

object Profile {
  def apply(x: (Int, String, String, String, Boolean, DateTime, DateTime)) : Profile =
    Profile(x._1, x._2, x._3, x._4, x._5, x._6, x._7)
}