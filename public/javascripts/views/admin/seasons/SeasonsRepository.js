/* global idl, routes */
'use strict';

var admin = admin || {};
admin.seasons = admin.seasons || {};
admin.seasons.SeasonsRepository = (function(ajax, routes) {
	var Repository = function() {
		this.removeSeason = function(id, context) {
			return ajax.post(routes.controllers.SeasonController.remove(id).url, {}, context);
		};

		this.assignTeamsToSeason = function(seasonId, teamIds, context) {
			return ajax.post(routes.controllers.SeasonController.assignTeamsToSeason(seasonId).url, teamIds, context);
		};

		this.removeTeamsFromSeason = function(seasonId, teamIds, context) {
			return ajax.post(routes.controllers.SeasonController.removeTeamsFromSeason(seasonId).url, teamIds, context);
		};
	};

	return Repository;
})(idl.ajax, routes);