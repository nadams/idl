package extensions

import org.joda.time.{ DateTime, DateTimeZone }
import org.joda.time.format.ISODateTimeFormat

object DateTimeExtensions {
  implicit class StringToDateTime(val input: String) extends AnyVal {
    def toDateTime = try { 
      ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(input) 
    } catch { 
      case _: Throwable => new DateTime(DateTimeZone.UTC) 
    }
  }
}
