/* global ko, _ */
'use strict';

var admin = admin || {};
admin.teams = admin.teams || {};
admin.teams.index = admin.teams.index || {};

admin.teams.index.IndexModel = (function(ko, _) {
	var Model = function(data, repository) {
		this.availableSeasons = [];
		this.selectedSeason = ko.observable();

		this.initialize(data);

		this.selectedSeason.subscribe(function(newSeason) {
			var promise = repository.getTeamList(newSeason.seasonId, this);
			promise.success(function() {
				console.log('success');
			});
		});
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.availableSeasons = _.map(data.seasons, function(seasonData) {
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
	var model = new admin.teams.index.IndexModel(admin.teams.index.data, new admin.teams.TeamRepository());

	ko.applyBindings(model);
})(ko);