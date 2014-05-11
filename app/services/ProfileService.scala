package services

import data._

trait ProfileServiceComponent {
	val profileService: ProfileService

	trait ProfileService {
		def getByUsername(username: String) : Option[Profile]
		def authenticate(username: String, password: String) : Boolean
		def updateProfilePassword(username: String, password: String) : Boolean
	}
}

trait ProfileServiceComponentImpl extends ProfileServiceComponent {
	self: ProfileRepositoryComponent =>
	override val profileService: ProfileService = new ProfileServiceImpl

	private class ProfileServiceImpl extends ProfileService {
		import io.github.nremond._

		val hasher = SecureHash(dkLength = 64)

		override def getByUsername(username: String) : Option[Profile] =
			profileRepository.getByUsername(username)

		override def authenticate(username: String, password: String) : Boolean =
			password != "" && getByUsername(username).exists(checkCredentials(_, password))

		override def updateProfilePassword(username: String, password: String) : Boolean = 
			password != "" && getByUsername(username).exists { profile =>
				val updatedProfile = Profile(
					profile.profileId, 
					profile.email, 
					hashPassword(password), 
					profile.passwordExpired, 
					profile.dateCreated, 
					profile.lastLoginDate
				)

				profileRepository.updateProfile(updatedProfile)
			}

		private def checkCredentials(profile: Profile, givenPassword: String) =
			hasher.validatePassword(givenPassword, profile.password)

		private def hashPassword(password: String) : String = 
			hasher.createHash(password)
	}
}
