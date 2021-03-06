package services

import data._
import security._

trait ProfileServiceComponent {
  val profileService: ProfileService

  trait ProfileService {
    def getByUsername(username: String) : Option[Profile]
    def getById(profileId: Int) : Option[Profile]
    def authenticate(username: String, password: String) : Boolean
    def updateProfilePassword(username: String, password: String) : Boolean
    def getProfileIdByUsername(username: String) : Option[Int]
    def profileIsInRole(username: String, role: Roles.Role): Boolean
    def profileIsInAnyRole(username: String, roles: Set[Roles.Role]): Boolean
    def createProfile(email: String, displayName: String, password: String) : Profile
    def updateDisplayName(profile: Profile, displayName: String) : Option[Profile]
    def searchProfiles(name: String) : Seq[ProfileSearchRecord]
    def updateLastLoginDate(username: String) : Boolean
    def getRolesForUsername(username: String) : Seq[Roles.Role]
    def updateProfile(profile: Profile) : Option[Profile]
    def addProfileToRoles(profileId: Int, roles: Seq[Int]) : Seq[Int]
    def removeProfileFromRoles(profileId: Int, roles: Seq[Int]) : Seq[Int]
  }
}

trait ProfileServiceComponentImpl extends ProfileServiceComponent {
  self: ProfileRepositoryComponent =>
  val profileService = new ProfileServiceImpl

  class ProfileServiceImpl extends ProfileService {
    import io.github.nremond._
    import org.joda.time.{ DateTime, DateTimeZone }

    val hasher = SecureHash(dkLength = 64)

    def getByUsername(username: String) : Option[Profile] =
      profileRepository.getByUsername(username)

    def getById(profileId: Int) = profileRepository.getById(profileId)

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

    def getProfileIdByUsername(username: String) : Option[Int] = getByUsername(username).map(_.profileId)

    def checkCredentials(profile: Profile, givenPassword: String) = hasher.validatePassword(givenPassword, profile.password)

    def hashPassword(password: String) = hasher.createHash(password)

    def createProfile(email: String, displayName: String, password: String) : Profile = {
      val now = new DateTime(DateTimeZone.UTC)

      val profile = profileRepository.insertProfile(
        Profile(0, email, displayName, hashPassword(password), false, now, now)
      )

      profileRepository.addProfileToRole(profile.profileId, Roles.User)

      profile
    }

    def profileIsInRole(username: String, role: Roles.Role): Boolean = 
      profileRepository.getRolesForUsername(username).exists { profileRole => 
        profileRole == Roles.SuperAdmin || profileRole == role
      }

    def profileIsInAnyRole(username: String, roles: Set[Roles.Role]): Boolean = {
      val profileRoles = profileRepository.getRolesForUsername(username) toSet

      profileRoles(Roles.SuperAdmin) || roles.intersect(profileRoles).nonEmpty
    }

    def updateDisplayName(profile: Profile, displayName: String) = {
      val updatedProfile = profile.copy(displayName = displayName)
  
      if(profileRepository.updateProfile(updatedProfile)) Some(updatedProfile)
      else None
    }

    def searchProfiles(name: String) = profileRepository.searchProfiles(name)
    def updateLastLoginDate(username: String) = profileRepository.updateLastLoginDate(username)
    def getRolesForUsername(username: String) = profileRepository.getRolesForUsername(username)
    def updateProfile(profile: Profile) = 
      if(profileRepository.updateProfile(profile)) Some(profile)
      else None

    def removeProfileFromRoles(profileId: Int, roles: Seq[Int]) =
      roles.filterNot(_ == Roles.SuperAdmin.id).filter(roleId => profileRepository.removeProfileFromRole(profileId, roleId))

    def addProfileToRoles(profileId: Int, roles: Seq[Int]) =
      roles.filter(roleId => profileRepository.assignProfileToRole(profileId, roleId))
  }
}
