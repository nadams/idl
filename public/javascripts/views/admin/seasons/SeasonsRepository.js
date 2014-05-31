/* global idl, routes */
'use strict';

var admin = admin || {};
admin.seasons = admin.seasons || {};
admin.seasons.SeasonsRepository = (function(ajax, routes) {
	var Repository = function() {

	};

	return Repository;
})(idl.ajax, routes);