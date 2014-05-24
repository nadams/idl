/* global ko */
'use strict';

var admin = admin || {};
admin.seasons = admin.seasons || {};
admin.seasons.edit = admin.seasons.edit || {};

admin.seasons.edit.EditSeasonModel = (function(ko) {
	var Model = function(data) {
		this.seasonId = 0;
		this.name = ko.observable('');
		this.startDate = ko.observable('');
		this.endDate = ko.observable('');

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.seasonId = data.seasonId;
			this.name(data.name);
			this.startDate(data.startDate);
			this.endDate(data.endDate);
		}
	});

	return Model;
})(ko);

(function(ko) {
	var model = new admin.seasons.edit.EditSeasonModel(admin.seasons.edit.data);

	ko.applyBindings(model);
})(ko);