package data

trait ProfileRepositoryComponent {
	val profileRepository: ProfileRepository

	trait ProfileRepository {
		def getByUsername(username: String) : Option[Profile]
		def updateProfile(profile: Profile) : Boolean
	}
}

trait ProfileRepositoryComponentImpl extends ProfileRepositoryComponent {
	override val profileRepository : ProfileRepository = new ProfileRepositoryImpl

	trait ProfileSchema {
		val tableName = "Profile"
		val profileId = "ProfileId"
		val email = "Email"
		val password = "Password"
		val dateCreated = "DateCreated"
		val passwordExpired = "PasswordExpired"
		val lastLoginDate = "LastLoginDate"
	}

	private class ProfileRepositoryImpl extends ProfileRepository with ProfileSchema {
		import java.sql._
		import anorm._ 
		import anorm.SqlParser._
		import org.joda.time.DateTime
		import play.api.db.DB
		import play.api.Play.current
		import AnormExtensions._

		override def getByUsername(username: String) : Option[Profile] = DB.withConnection { implicit connection =>
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

		override def updateProfile(profile: Profile) : Boolean = DB.withConnection { implicit connection =>
			SQL(
				f"""
					UPDATE $tableName
					SET 
						$email = {email},
						$password = {password},
						$dateCreated = {dateCreated},
						$passwordExpired = {passwordExpired},
						$lastLoginDate = {lastLoginDate}
					WHERE $profileId = {profileId}
				"""
			).on(
				"profileId" -> profile.profileId,
				"email" -> profile.email,
				"password" -> profile.password,
				"dateCreated" -> profile.dateCreated,
				"passwordExpired" -> profile.passwordExpired,
				"lastLoginDate" -> profile.lastLoginDate
			).executeUpdate() > 0
		}

		private def mapProfile(query: SimpleSql[Row])(implicit connection: Connection) : Option[Profile] = 
			query.singleOpt(int(profileId) ~ str(email) ~ str(password) ~ bool(passwordExpired) ~ get[DateTime](dateCreated) ~ get[DateTime](lastLoginDate) map(flatten))
			.map(x => Profile(x._1, x._2, x._3, x._4, x._5, x._6))
	}
}
