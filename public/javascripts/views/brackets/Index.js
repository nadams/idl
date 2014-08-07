/* global ko, _, brackets */

brackets.index.IndexModel = (function(ko, _) {
	'use strict';

	var Model = function(data) {
		this.stats = ko.observableArray();

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.stats(_.map(data.stats, function(item) {
				return new brackets.index.TeamStatsModel(item);
			}, this));
		}
	});

	return Model;
})(ko, _);

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