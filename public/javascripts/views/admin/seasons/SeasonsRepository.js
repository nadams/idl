/* global idl, routes */
'use strict';

var admin = admin || {};
admin.seasons = admin.seasons || {};
admin.seasons.SeasonsRepository = (function(ajax, routes) {
	var Repository = function() {
		this.removeSeason = function(id, context) {
			return ajax.post(routes.controllers.SeasonController.remove(id).url, {}, context);
		};
	};

	return Repository;
})(idl.ajax, routes);