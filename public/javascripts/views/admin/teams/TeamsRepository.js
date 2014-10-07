/* global routes, idl */
'use strict';

var admin = admin || {};
admin.teams = admin.teams || {};
admin.teams.TeamRepository = (function(ajax, routes) {
	var Repository = function() {
		this.getTeamList = function(seasonId, context) {
			return ajax.get(routes.controllers.TeamController.getTeamList(seasonId).url, context);
		};

		this.getPlayers = function(context) {
			return ajax.get(routes.controllers.TeamController.getPlayers().url, context);
		};

		this.assignPlayersToTeam = function(teamId, playerIds, context) {
			var data = {
				teamId: teamId,
				playerIds: playerIds
			};

			return ajax.post(routes.controllers.TeamController.assignPlayers().url, data, context);
		};

		this.removePlayersFromTeam = function(teamId, playerIds, context) {
			var data = {
				teamId: teamId,
				playerIds: playerIds
			};

			return ajax.post(routes.controllers.TeamController.removePlayers().url, data, context);
		};

		this.removeTeam = function(teamId, context) {
			return ajax.post(routes.controllers.TeamController.remove(teamId).url, {}, context);
		};
	};

	return Repository;
})(idl.ajax, routes);
