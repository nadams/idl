package services

import data._

trait ProfileServiceComponent {
	val profileService: ProfileService

	trait ProfileService {
		def getByUsername(username: String) : Option[Profile]
		def authenticate(username: String, password: String) : Boolean
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
			getByUsername(username).exists(checkCredentials(_, password))

		private def checkCredentials(profile: Profile, givenPassword: String) =
			hasher.validatePassword(givenPassword, profile.password)
	}
}
