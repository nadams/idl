package models

case class ProfileModel(username: String, password: String)

object ProfileModel {
	def apply() : ProfileModel = ProfileModel("", "")
}
