/* global ko, _ */
'use strict';

var admin = admin || {};
admin.teams = admin.teams || {};
admin.teams.index = admin.teams.index || {};

admin.teams.index.IndexModel = (function(ko, _) {
	var Model = function(data) {

		this.initialize(data);
	};

	ko.utils.extend(Model, {
		initialize: function(data) {

		}
	});

	return Model;
})(ko, _);