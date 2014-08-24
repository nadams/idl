package security

object Roles extends Enumeration {
  type Role = Value

  val SuperAdmin = Value(1, "SuperAdmin")
  val Admin = Value(2, "Admin")
  val User = Value(3, "User")
}
