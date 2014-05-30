package models

case class MenuModel(username: Option[String], canSeeAdminMenu: Boolean)

object MenuModel {
  def apply() : MenuModel = MenuModel(None, false)
}
