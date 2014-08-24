/* global admin, routes, ko, _ */

'use strict';

admin.seasons = admin.seasons || {};
admin.seasons.teamSeasons = admin.seasons.teamSeasons || {};
admin.seasons.teamSeasons.TeamSeasonModel = (function(ko, _) {
	var Model = function(data, repository) {
		this.repository = repository;

		this.seasonId = 0;
		this.seasonName = '';
		this.allTeams = [];
		this.assignedTeams = ko.observableArray([]);

		this.selectedAvailableTeams = ko.observableArray([]);
		this.selectedTeamsInSeason = ko.observableArray([]);

		this.initialize(data);

		this.teamsInSeason = ko.computed(function() {
			return _.filter(this.allTeams, function(item) {
				return _.contains(this.assignedTeams(), item.teamId);
			}, this);
		}, this);

		this.teamsNotInSeason = ko.computed(function() {
			return _.filter(this.allTeams, function(item) {
				return !_.contains(this.assignedTeams(), item.teamId);
			}, this);
		}, this);

		this.canAssignTeams = ko.computed(function() {
			return this.selectedAvailableTeams().length > 0;
		}, this);

		this.canRemoveTeams = ko.computed(function() {
			return this.selectedTeamsInSeason().length > 0;
		}, this);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.seasonId = data.seasonId;
			this.seasonName = data.seasonName;
			this.allTeams = data.allTeams;

			_.each(data.assignedTeams, function(teamId) {
				this.assignedTeams.push(teamId);
			}, this);
		},
		assignTeamsToSeason: function() {
			this.selectedTeamsInSeason.removeAll();

			var teamIds = _.pluck(this.selectedAvailableTeams(), 'teamId');
			var promise = this.repository.assignTeamsToSeason(this.seasonId, teamIds, this);

			promise.done(function(data) {
				_.each(data, function(teamId) {
					this.assignedTeams.push(teamId);
				}, this);
			});
		},
		removeTeamsFromSeason: function() {
			this.selectedAvailableTeams.removeAll();

			var teamIds = _.pluck(this.selectedTeamsInSeason(), 'teamId');
			var promise = this.repository.removeTeamsFromSeason(this.seasonId, teamIds, this);

			promise.done(function(data) {
				_.each(data, function(teamId) {
					this.assignedTeams.remove(teamId);
				}, this);
			});
		}
	});

	return Model;
})(ko, _);

(function(admin, routes, ko) {
	var repository = new admin.seasons.SeasonsRepository(routes);
	var model = new admin.seasons.teamSeasons.TeamSeasonModel(admin.seasons.teamSeasons.data, repository);

	ko.applyBindings(model);
})(admin, routes, ko);