package services

import data._

trait ProfileServiceComponent {
  val profileService: ProfileService

  trait ProfileService {
    def getByUsername(username: String) : Option[Profile]
    def authenticate(username: String, password: String) : Boolean
    def updateProfilePassword(username: String, password: String) : Boolean
    def getProfileIdByUsername(username: String) : Option[Int]
  }
}

trait ProfileServiceComponentImpl extends ProfileServiceComponent {
  self: ProfileRepositoryComponent =>
  val profileService: ProfileService = new ProfileServiceImpl

  class ProfileServiceImpl extends ProfileService {
    import io.github.nremond._

    val hasher = SecureHash(dkLength = 64)

    def getByUsername(username: String) : Option[Profile] =
      profileRepository.getByUsername(username)

    def authenticate(username: String, password: String) : Boolean =
      password != "" && getByUsername(username).exists(checkCredentials(_, password))

    def updateProfilePassword(username: String, password: String) : Boolean = 
      password != "" && getByUsername(username).exists { profile =>
        val updatedProfile = Profile(
          profile.profileId, 
          profile.email, 
          profile.displayName,
          hashPassword(password), 
          profile.passwordExpired, 
          profile.dateCreated, 
          profile.lastLoginDate
        )

        profileRepository.updateProfile(updatedProfile)
      }

    def getProfileIdByUsername(username: String) : Option[Int] =
      getByUsername(username).map(_.profileId)

    def checkCredentials(profile: Profile, givenPassword: String) =
      hasher.validatePassword(givenPassword, profile.password)

    def hashPassword(password: String) : String = 
      hasher.createHash(password)
  }
}
