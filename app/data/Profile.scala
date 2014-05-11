package data

import com.github.nscala_time.time.Imports._

case class Profile(profileId: Int, email: String, password: String, dateCreated: DateTime, passwordExpired: Boolean, lastLoginDate: DateTime)