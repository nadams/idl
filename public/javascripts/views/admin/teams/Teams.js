/* global ko, _, admin */

'use strict';

admin.teams = admin.teams || {};
admin.teams.IndexModel = (function(ko, _) {
	var Model = function(data) {
		this.teams = ko.observableArray([]);

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.teams(_.map(data.teams, function(team) {
				return new admin.teams.TeamModel(team);
			}, this));
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
		this.removeUrl = '';

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
			this.removeUrl = data.removeUrl;
		},
		showConfirmRemoveTeam: function() {
			this.confirmRemoveTeam(true);
		},
		hideConfirmRemoveTeam: function() {
			this.confirmRemoveTeam(false);
		},
		removeTeam: function(team) {

		}
	});

	return Model;
})();

(function(ko, admin) {
	var model = new admin.teams.IndexModel(admin.teams.data);

	ko.applyBindings(model);
})(ko, admin);