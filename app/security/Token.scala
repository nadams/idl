package security

import java.security.SecureRandom

case class Token(token: String)

object Token {
  import data.PasswordTokenSchema
  val random = new SecureRandom()

  val insertToken =
    s"""
      INSERT INTO ${PasswordTokenSchema.tableName} (
        ${PasswordTokenSchema.profileId},
        ${PasswordTokenSchema.token},
        ${PasswordTokenSchema.dateCreated},
        ${PasswordTokenSchema.isClaimed}
      ) VALUES (
        {profileId},
        {token},
        {dateCreated},
        {isClaimed}
      )
    """

  def apply(length: Int = 48) : Token =
    Token(randomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_")(length))

  private def randomString(values: String)(n: Int) =
    Stream.continually(random.nextInt(values.size)).map(values).take(n).mkString
}

