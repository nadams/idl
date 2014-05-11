package data

import com.github.nscala_time.time.Imports._

case class Profile(profileId: Int, email: String, password: String, passwordExpired: Boolean, dateCreated: DateTime, lastLoginDate: DateTime)