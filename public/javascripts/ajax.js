/* global jQuery */
'use strict';

var idl = idl || {};
idl.ajax = (function($) {
	var Ajax = {
		get: function(url, context) {
			return $.ajax({
				url: url,
				type: 'GET',
				dataType: 'json',
				context: context
			});
		},
		post: function(url, data, context) {
			return $.ajax({
				url: url,
				data: JSON.stringify(data),
				type: 'POST',
				dataType: 'json',
				contentType: 'application/json',
				context: context
			});
		}
	};

	return Ajax;
})(jQuery);