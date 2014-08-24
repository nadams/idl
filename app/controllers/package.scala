import play.api._
import play.api.mvc._
import play.api.libs.json._

package object controllers {
  private val invalidJsonFormat = Results.BadRequest("Invalid json format")

  def handleJsonPost[T](action: T => JsValue)(implicit reads: Reads[T], request: Request[AnyContent]) = request.body.asJson match {
    case Some(jsValue) => {
      Json.fromJson[T](jsValue).asOpt match {
        case Some(x) => Results.Ok(action(x))
        case None => invalidJsonFormat
      }
    }
    case None => invalidJsonFormat
  }
}