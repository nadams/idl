package services

import data._

trait ProfileServiceComponent {
	val profileService: ProfileService

	trait ProfileService {
		def getByUsername(username: String) : Option[Profile]
	}
}

trait ProfileServiceComponentImpl extends ProfileServiceComponent {
	self: ProfileRepositoryComponent =>
	override val profileService: ProfileService = new ProfileServiceImpl

	private class ProfileServiceImpl extends ProfileService {
		override def getByUsername(username: String) : Option[Profile] =
			profileRepository.getByUsername(username)
	}
}
