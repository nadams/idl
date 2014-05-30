package security

object Roles extends Enumeration {
  type Role = Value

  val SuperAdmin = Value(1, "SuperAdmin")
}
