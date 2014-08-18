/* global ko, _, brackets */

brackets.index.IndexModel = (function(ko, _) {
	'use strict';

	var Model = function(data) {
		this.collectionUtils = new brackets.index.CollectionUtils();

		this.groupByWeek = function() {
			return this.collectionUtils.multiGroup(this.playoffStats(), function(item) {
				return item.weekId();
			});
		};

		this.playoffStats = ko.observableArray([]);
		this.regularSeasonStats = ko.observableArray([]);

		this.initialize(data);

		this.teams = ko.computed(function() {
			return _.map(this.groupByWeek(), function(item) {
				return this.collectionUtils.splitArray(_.map(item, function(data) {
					return data.teamName();
				}), 2);
			}, this);
		}, this);

		this.results = ko.computed(function() {
			return _.map(this.groupByWeek(), function(item) {
				return this.collectionUtils.splitArray(_.map(item, function(data) {
					return data.captures();
				}), 2);
			}, this);
		}, this);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.playoffStats(_.map(data.playoffStats, function(item) {
				return new brackets.index.TeamStatsModel(item);
			}, this));

			this.regularSeasonStats(_.map(data.regularStats, function(item) {
				return new brackets.index.RegularSeasonStatsModel(item);
			}, this));
		}
	});

	return Model;
})(ko, _);

brackets.index.CollectionUtils = (function(_) {
	'use strict';

	var Utils = function() {
		return {
			multiGroup: function(arr, keySelector) {
				var grouped = _.groupBy(arr, function(item) {
					return keySelector(item);
				});

				var result = [];

				for (var key in grouped) {
					if (grouped.hasOwnProperty(key)) {
						var val = grouped[key];

						result.push(val);
					}
				}

				return result;
			},
			splitArray: function(arr, chunk) {
				var result = [];
				var i, j;

				for (i = 0, j = arr.length; i < j; i += chunk) {
					result.push(arr.slice(i, i + chunk));
				}

				return result;
			}
		};
	};

	return Utils;
})(_);

brackets.index.RegularSeasonStatsModel = (function(ko) {
	'use strict';

	var Model = function(data) {
		this.teamId = ko.observable();
		this.teamName = ko.observable();
		this.wins = ko.observable();
		this.losses = ko.observable();
		this.ties = ko.observable();

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.teamId(data.teamId);
			this.teamName(data.teamName);
			this.wins(data.wins);
			this.losses(data.losses);
			this.ties(data.ties);
		}
	});

	return Model;
})(ko);

brackets.index.TeamStatsModel = (function(ko) {
	'use strict';

	var Model = function(data) {
		this.teamId = ko.observable();
		this.teamName = ko.observable();
		this.gameId = ko.observable();
		this.weekId = ko.observable();
		this.captures = ko.observable();

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.teamId(data.teamId);
			this.teamName(data.teamName);
			this.gameId(data.gameId);
			this.weekId(data.weekId);
			this.captures(data.captures);
		}
	});

	return Model;
})(ko);

(function(ko, brackets) {
	'use strict';

	var model = new brackets.index.IndexModel(brackets.index.data);
	ko.applyBindings(model);
})(ko, brackets);