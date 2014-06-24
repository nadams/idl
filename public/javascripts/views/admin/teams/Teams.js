/* global ko, _, admin */

'use strict';

admin.teams = admin.teams || {};
admin.teams.IndexModel = (function(ko, _) {
	var Model = function(data, repo) {
		this.repo = repo;
		this.teams = ko.observableArray([]);

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.teams(_.map(data.teams, function(team) {
				return new admin.teams.TeamModel(team);
			}, this));
		},
		removeTeam: function(team) {
			team.isRemovingTeam(true);

			var promise = this.repo.removeTeam(team.teamId, this);

			promise.always(function() {
				team.isRemovingTeam(false);
				team.hideConfirmRemoveTeam();
			});

			promise.done(function(data) {
				if(data) {
					this.teams.remove(_.find(this.teams(), function(team) {
						return data == team.teamId;
					}, this));
				}
			});
		}
	});

	return Model;
})(ko, _);

admin.teams.TeamModel = (function() {
	var Model = function(data) {
		this.teamId = 0;
		this.teamName = '';
		this.isActive = false;
		this.editUrl = '';

		this.confirmRemoveTeam = ko.observable(false);
		this.isRemovingTeam = ko.observable(false);

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.teamId = data.teamId;
			this.teamName = data.teamName;
			this.isActive = data.isActive;
			this.editUrl = data.editUrl;
		},
		showConfirmRemoveTeam: function() {
			this.confirmRemoveTeam(true);
		},
		hideConfirmRemoveTeam: function() {
			this.confirmRemoveTeam(false);
		}
	});

	return Model;
})();

(function(ko, admin) {
	var model = new admin.teams.IndexModel(admin.teams.data, new admin.teams.TeamRepository());

	ko.applyBindings(model);
})(ko, admin);