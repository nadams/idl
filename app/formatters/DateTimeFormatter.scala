package formatters

import play.api.libs.json._
import play.api.data.validation._
import org.joda.time.{ DateTime, DateTimeZone }
import org.joda.time.format.ISODateTimeFormat

object DateTimeFormatter {
  implicit val writesDateTime: Format[DateTime] = new Format[DateTime] {
    def writes(d: DateTime) = JsString(ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).print(d))
    def reads(json: JsValue) = json match {
      case JsString(dateString) => JsSuccess(DateTime.parse(dateString, ISODateTimeFormat.dateTime.withZone(DateTimeZone.UTC)))
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date"))))
    }
  }
}