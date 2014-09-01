import play.api._
import play.api.mvc._
import play.api.libs.json._

package object controllers {
  private val invalidJsonFormat = Results.BadRequest("Invalid json format")

  def handleJsonPost[T](action: T => Result)(implicit reads: Reads[T], request: Request[AnyContent]) = request.body.asJson map { jsValue =>
    Json.fromJson[T](jsValue).asOpt.map(x => action(x)).getOrElse(invalidJsonFormat)
  } getOrElse(invalidJsonFormat)
}
