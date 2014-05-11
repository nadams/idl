package models

import play.api.data.{ Field, Form }
import play.api.i18n.{ Messages, Lang }

object FieldExtensions {
	class FieldError(field: Field) {
		def formattedMessage : Pair[String, Option[String]] =
			(field.value.getOrElse(""), field.error.map { error => Messages(error.message, error.args: _*) })
	}

	implicit def formattedMessage(field: Field) = new FieldError(field)
}

object FormExtensions {
	class FormError[T](form: Form[T]) {
		def formattedMessages : Seq[String] =
			form.globalErrors.map { error => Messages(error.message, error.args: _*) }
	}

	implicit def formattedMessages[T](form: Form[T]) = new FormError(form)
}
