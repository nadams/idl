package models

import play.api.data.{ Field, Form }

object FieldExtensions {
	class FieldError(field: Field) {
		def formattedMessage : Pair[String, Option[String]] =
			(field.value.getOrElse(""), field.error.map(_.message))
	}

	implicit def formattedMessage(field: Field) = new FieldError(field)
}

object FormExtensions {
	class FormError[T](form: Form[T]) {
		def formattedMessages : Seq[String] =
			form.globalErrors.map(_.message)
	}

	implicit def formattedMessages[T](form: Form[T]) = new FormError(form)
}
