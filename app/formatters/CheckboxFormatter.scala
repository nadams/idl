package formatters

import play.api.data._
import play.api.data.format._

object CheckboxFormatter {
  val checkbox : Mapping[Boolean] = FieldMapping[Boolean]()(CheckboxMapping.checkbox)

  object CheckboxMapping {
    def checkbox: Formatter[Boolean] = new Formatter[Boolean] {
      override val format = Some(("format.boolean", Nil))

      def bind(key: String, data: Map[String, String]) = {
        Right(data.get(key).getOrElse("")).right.flatMap {
          case "on" => Right(true)
          case "" => Right(false)
          case _ => Left(Seq(FormError(key, "error.boolean", Nil)))
        }
      }

      def unbind(key: String, value: Boolean) = Map(key -> value.toString)
    }
  }
}
