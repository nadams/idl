/* global ko, jQuery */

(function(ko, $) {
	'use strict';

	ko.bindingHandlers.brackets = {
		init: function(element, valueAccessor) {
			var values = valueAccessor();

			var data = values.data; 

			$(element).bracket({
				init: data
			});
		}
	};
})(ko, jQuery);
