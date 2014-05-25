package models.admin.seasons

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import org.joda.time.{ DateTime, DateTimeZone }
import org.joda.time.format.ISODateTimeFormat
import com.github.nscala_time.time.Imports._
import formatters.DateTimeFormatter
import play.api.data.format.Formats._
import play.api.data.format._

case class EditSeasonModel(seasonId: Int, name: String, startDate: DateTime, endDate: DateTime) {
  val isNewSeason = seasonId == 0
}

object EditSeasonModel {

  implicit val writes = Json.writes[EditSeasonModel]

  lazy val now = new DateTime(DateTimeZone.UTC)
  lazy val empty = EditSeasonModel(0, "", now, now)
}

case class EditSeasonModelErrors(nameError: Option[String], startDateError: Option[String], endDateError: Option[String], globalErrors: Seq[String])

object EditSeasonModelErrors {
  val empty = EditSeasonModelErrors(None, None, None, Seq.empty[String])
}

object EditSeasonModelForm {
  import DateTimeMapping._

  def apply() = Form(
    mapping(
      "seasonId" -> number,
      "name" -> nonEmptyText,
      "startDate" -> jodaISODate(),
      "endDate" -> jodaISODate()
    )(EditSeasonModel.apply)(EditSeasonModel.unapply)
    verifying("error.startDateMustBeBeforeEndDate", result => result.startDate < result.endDate)
  )
}

object DateTimeMapping {
  def jodaISODateTimeFormat(timezone: org.joda.time.DateTimeZone = DateTimeZone.UTC): Formatter[DateTime] = new Formatter[DateTime] {
    val formatter = ISODateTimeFormat.dateTime().withZone(timezone)
    override val format = Some(("format.date", Seq(Nil)))
    def bind(key: String, data: Map[String, String]) = parsing(formatter.parseDateTime, "error.date", Nil)(key, data)
    def unbind(key: String, value: DateTime) = Map(key -> formatter.print(value.withZone(timezone)))
  }

  def parsing[T](parse: String => T, errMsg: String, errArgs: Seq[Any])(key: String, data: Map[String, String]): Either[Seq[FormError], T] = {
    stringFormat.bind(key, data).right.flatMap { s =>
      scala.util.control.Exception.allCatch[T]
        .either(parse(s))
        .left.map(e => Seq(FormError(key, errMsg, errArgs)))
    }
  }

  def jodaISODate(timezone: DateTimeZone = DateTimeZone.UTC) : Mapping[DateTime] = Forms.of[DateTime] as jodaISODateTimeFormat(timezone)
}
