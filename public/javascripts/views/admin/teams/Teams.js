/* global ko, _ */
'use strict';

var admin = admin || {};
admin.teams = admin.teams || {};
admin.teams.index = admin.teams.index || {};

admin.teams.index.IndexModel = (function(ko, _) {
	var Model = function(data) {
		this.seasons = [];
		this.selectedSeason = ko.observable();

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.seasons = _.map(data, function(seasonData) {
				return new admin.teams.index.SeasonModel(seasonData);
			}, this);
		}
	});

	return Model;
})(ko, _);

admin.teams.index.SeasonModel = (function(ko) {
	var Model = function(data) {
		this.seasonId = 0;
		this.seasonName = '';

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.seasonId = data.seasonId;
			this.seasonName = data.seasonName;
		}
	});

	return Model;
})(ko);

(function(ko) {
	var model = new admin.teams.index.IndexModel(admin.teams.index.data);

	ko.applyBindings(model);
})(ko);