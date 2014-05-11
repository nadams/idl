package data

trait ProfileRepository {
	def getByUsername(username: String) : Option[Profile]
}

trait ProfileSchema {
	val tableName = "Profile"
	val profileId = "ProfileId"
	val email = "Email"
	val password = "Password"
	val dateCreated = "DateCreated"
	val passwordExpired = "PasswordExpired"
	val lastLoginDate = "LastLoginDate"
}

class ProfileRepositoryImpl extends ProfileRepository with ProfileSchema {
	import java.sql._
	import anorm._ 
	import anorm.SqlParser._
	import org.joda.time.DateTime
	import play.api.db.DB
	import play.api.Play.current
	import AnormExtensions._

	def getByUsername(username: String) : Option[Profile] = DB.withConnection { implicit connection =>
		val profile = SQL(
		  f"""
		  	SELECT *
		  	FROM $tableName
		  	WHERE $email = {email}
		  """
		)
		.on("email" -> username)
		
	  mapProfile(profile)
	}

	private def mapProfile(query: SimpleSql[Row])(implicit connection: Connection) : Option[Profile] = 
		query.singleOpt(int(profileId) ~ str(email) ~ str(password) ~ bool(passwordExpired) ~ get[DateTime](dateCreated) ~ get[DateTime](lastLoginDate) map(flatten))
		.map(x => Profile(x._1, x._2, x._3, x._4, x._5, x._6))
}
