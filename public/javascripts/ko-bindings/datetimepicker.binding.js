/* global ko, jQuery */

(function(ko, $) {
	'use strict';

	ko.bindingHandlers.datetimepicker = {
		init: function(element, valueAccessor, allBindingsAccessor) {
			var options = allBindingsAccessor().datepickerOptions || {},
				datetimepicker = $(element).parent().datetimepicker(options);

			ko.utils.registerEventHandler(datetimepicker, 'dp.change', function() {
				valueAccessor()(datetimepicker.datetimepicker().data('DateTimePicker').getDate());
			});

			return datetimepicker;
		},

		update: function(element, valueAccessor) {
			$(element).datetimepicker().data('DateTimePicker').setDate(ko.utils.unwrapObservable(valueAccessor()));
		}
	};
})(ko, jQuery);