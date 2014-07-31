/* global jQuery, ko, admin */

admin.games.stats.StatsModel = (function(ko) {
	'use strict';

	var Model = function(data) {
		this.seasonId = 0;
		this.gameId = 0;
		this.stats = ko.observable();

		this.initialize(data);

		this.hasStats = ko.computed(function() {
			return this.stats() !== undefined;
		}, this);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.seasonId = data.seasonId;
			this.gameId = data.gameId;
			this.stats(data.stats);
		},
		uploadStats: function() {}
	});

	return Model;
})(ko);

(function($, ko, admin) {
	'use strict';

	// $('#statsUpload').fileUpload({

	// });

	var model = new admin.games.stats.StatsModel(admin.games.stats.data);
	ko.applyBindings(model);
})(jQuery, ko, admin);