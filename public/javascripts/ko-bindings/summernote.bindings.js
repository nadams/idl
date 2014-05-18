/* global ko, $ */

(function(ko, $) {
	'use strict';

	ko.bindingHandlers.summernote = {
		init: function(element, valueAccessor, allBindingsAccessor) {
			var options = valueAccessor();
			options.height = 300;
			options.focus = true;
			options.onblur = function() {
				return valueAccessor()($(element).code());
			};

			return $(element).summernote(options);
		},

		/* jshint ignore:start */
		update: function(element, valueAccessor, allBindingsAccessor) {
			$(element).code(ko.utils.unwrapObservable(valueAccessor()));
		}
		/* jshint ignore:end */
	};
})(ko, $);