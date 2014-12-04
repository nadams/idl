package security

import java.security.SecureRandom

object Token {
  val random = new SecureRandom()

  def apply(length: Int = 48) : String = 
    randomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_")(length)

  private def randomString(values: String)(n: Int) =
    Stream.continually(random.nextInt(values.size)).map(values).take(n).mkString
}

