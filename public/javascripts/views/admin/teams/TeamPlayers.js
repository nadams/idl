/* global ko, _ */
'use strict';

var admin = admin || {};
admin.teams = admin.teams || {};
admin.teams.index = admin.teams.index || {};

admin.teams.index.IndexModel = (function(ko, _) {
	var Model = function(data, repository) {
		this.repository = repository;

		this.availableSeasons = [];
		this.selectedSeason = ko.observable();

		this.availableTeams = ko.observableArray([]);
		this.selectedTeam = ko.observable();

		this.availablePlayers = ko.observableArray([]);
		this.selectedTeamPlayers = ko.observableArray([]);
		this.selectedAvailablePlayers = ko.observableArray([]);

		this.isLoadingTeams = ko.observable(false);
		this.isLoadingPlayers = ko.observable(false);
		this.isAssigningPlayersToTeam = ko.observable(false);
		this.isRemovingPlayersFromTeam = ko.observable(false);

		this.initialize(data);

		this.unassignedPlayers = ko.computed(function() {
			return _.filter(this.availablePlayers(), function(item) {
				return item.teamId() === undefined;
			}, this);
		}, this);

		this.playersInCurrentTeam = ko.computed(function() {
			var that = this;

			return _.filter(this.availablePlayers(), function(item) {
				return item.teamId() === this.selectedTeam().teamId;
			}, this).sort(function(left, right) {
				return that.sortStrings(right.playerName, left.playerName);
			});
		}, this);

		this.canAssignPlayers = ko.computed(function() {
			return this.selectedAvailablePlayers().length > 0 && !this.isAssigningPlayersToTeam();
		}, this);

		this.canRemovePlayers = ko.computed(function() {
			return this.selectedTeamPlayers().length > 0 && !this.isRemovingPlayersFromTeam();
		}, this);

		this.selectedSeason.subscribe(function(newSeason) {
			this.availableTeams.removeAll();
			this.selectedTeam(undefined);

			if (this.selectedSeason()) {
				this.isLoadingTeams(true);

				var promise = this.repository.getTeamList(newSeason.seasonId, this);

				promise.done(function(data) {
					var availableTeams = this.availableTeams();

					_.each(data.teams, function(item) {
						availableTeams.push(new admin.teams.index.TeamModel(item));
					}, this);

					this.availableTeams.valueHasMutated();
				});

				promise.always(function() {
					this.isLoadingTeams(false);
				});
			}
		}, this);

		this.selectedTeam.subscribe(function() {
			this.availablePlayers.removeAll();
			this.selectedTeamPlayers.removeAll();
			this.selectedAvailablePlayers.removeAll();

			if (this.selectedTeam()) {
				this.isLoadingPlayers(true);

				var promise = this.repository.getPlayers(this);
				promise.done(function(data) {
					var availablePlayers = this.availablePlayers();

					_.each(data.players, function(item) {
						availablePlayers.push(new admin.teams.index.PlayerModel(item));
					}, this);

					this.availablePlayers.valueHasMutated();
				});

				promise.always(function() {
					this.isLoadingPlayers(false);
				});
			}
		}, this);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.availableSeasons = _.map(data.seasons, function(seasonData) {
				return new admin.teams.index.SeasonModel(seasonData);
			}, this);
		},
		assignPlayersToCurrentTeam: function() {
			this.isAssigningPlayersToTeam(true);
			var selectedTeamId = this.selectedTeam().teamId,
				selectedPlayerIds = _.map(this.selectedAvailablePlayers(), function(player) {
					return player.playerId;
				}, this);

			var promise = this.repository.assignPlayersToTeam(selectedTeamId, selectedPlayerIds, this);

			promise.done(function(assignedPlayerIds) {
				var assignedPlayers = _.filter(this.availablePlayers(), function(item) {
					return _.contains(assignedPlayerIds, item.playerId);
				}, this);

				var currentTeamId = this.selectedTeam().teamId;

				_.forEach(assignedPlayers, function(player) {
					player.teamId(currentTeamId);
				}, this);
			});

			promise.always(function() {
				this.isAssigningPlayersToTeam(false);
			});
		},
		removePlayersFromCurrentTeam: function() {
			this.isRemovingPlayersFromTeam(true);

			var selectedTeamId = this.selectedTeam().teamId,
				selectedPlayerIds = _.map(this.selectedTeamPlayers(), function(player) {
					return player.playerId;
				}, this);

			var promise = this.repository.removePlayersFromTeam(selectedTeamId, selectedPlayerIds, this);

			promise.done(function(assignedPlayerIds) {
				var assignedPlayers = _.filter(this.availablePlayers(), function(item) {
					return _.contains(assignedPlayerIds, item.playerId);
				}, this);

				_.forEach(assignedPlayers, function(player) {
					player.teamId(undefined);
				}, this);
			});

			promise.always(function() {
				this.isRemovingPlayersFromTeam(false);
			});
		},
		sortStrings: function(lName, rName) {
			lName = lName.toUpperCase();
			rName = rName.toUpperCase();
			return lName === rName ? 0 : (lName < rName ? -1 : 1);
		}
	});

	return Model;
})(ko, _);

admin.teams.index.PlayerModel = (function(ko) {
	var Model = function(data) {
		this.playerId = 0;
		this.playerName = '';
		this.teamId = ko.observable();

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.playerId = data.playerId;
			this.playerName = data.playerName;
			this.teamId(data.teamId);
		}
	});

	return Model;
})(ko);

admin.teams.index.TeamModel = (function(ko) {
	var Model = function(data) {
		this.teamId = 0;
		this.teamName = '';

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.teamId = data.teamId;
			this.teamName = data.teamName;
		}
	});

	return Model;
})(ko);

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