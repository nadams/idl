/* global jQuery, routes */
'use strict';

var admin = admin || {};
admin.teams = admin.teams || {};
admin.teams.TeamRepository = (function($, routes) {
	var Repository = function() {
		this.get = function(url, context) {
			return $.ajax({
				url: url,
				type: 'GET',
				dataType: 'json',
				context: context
			});
		};

		this.post = function(url, data, context) {
			return $.ajax({
				url: url,
				data: JSON.stringify(data),
				type: 'POST',
				dataType: 'json',
				contentType: 'application/json',
				context: context
			});
		};

		this.getTeamList = function(seasonId, context) {
			return this.get(routes.controllers.TeamController.getTeamList(seasonId).url, context);
		};

		this.getPlayers = function(context) {
			return this.get(routes.controllers.TeamController.getPlayers().url, context);
		};

		this.assignPlayersToTeam = function(seasonId, teamId, playerIds, context) {
			var data = {
				seasonId: seasonId,
				teamId: teamId,
				playerIds: playerIds
			};

			return this.post(routes.controllers.TeamController.assignPlayers().url, data, context);
		};
	};

	return Repository;
})(jQuery, routes);