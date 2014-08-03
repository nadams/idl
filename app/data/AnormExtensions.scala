package data

import java.sql.Timestamp
import anorm._
import org.joda.time._
import org.joda.time.format._

object AnormExtensions {
  val dateFormatGeneration: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmssSS").withZoneUTC
  val utc = DateTimeZone.UTC

  implicit def rowToDateTime: Column[DateTime] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    
    value match {
      case ts: java.sql.Timestamp => Right(new DateTime(ts.getTime, utc))
      case d: java.sql.Date => Right(new DateTime(d.getTime, utc))
      case str: java.lang.String => Right(dateFormatGeneration.parseDateTime(str))  
      case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass) )
    }
  }

  implicit val dateTimeToStatement = new ToStatement[DateTime] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: DateTime): Unit = {
      s.setTimestamp(index, new Timestamp(aValue.withMillisOfSecond(0).getMillis()))
    }
  }

  implicit object byteArrayToStatement extends ToStatement[Array[Byte]] {
    import java.sql.PreparedStatement
    import java.io.ByteArrayInputStream

    def set(s: PreparedStatement, i: Int, data: Array[Byte]) : Unit = 
      s.setBinaryStream(i, new ByteArrayInputStream(data), data.length)
  }
}
