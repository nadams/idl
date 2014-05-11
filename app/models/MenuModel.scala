package models

case class MenuModel(username: Option[String])

object MenuModel {
  def apply() : MenuModel = MenuModel(None)
}
