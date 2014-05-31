/* global ko, moment, _ */
'use strict';

var admin = admin || {};
admin.seasons = admin.seasons || {};
admin.seasons.index = admin.seasons.index || {};

admin.seasons.index.SeasonModel = (function(ko, moment) {
	var Model = function(data) {
		this.seasonId = 0;
		this.name = '';
		this.startDate = ko.observable();
		this.endDate = ko.observable();
		this.editLink = '';
		this.removeLink = '';
		this.confirmationShown = ko.observable(false);

		this.initialize(data);

		this.niceStartDate = ko.computed(function() {
			return this.niceDate(this.startDate());
		}, this);

		this.niceEndDate = ko.computed(function() {
			return this.niceDate(this.endDate());
		}, this);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.seasonId = data.seasonId;
			this.name = data.name;
			this.startDate(data.startDate);
			this.endDate(data.endDate);
			this.editLink = data.editLink;
			this.removeLink = data.removeLink;
		},
		niceDate: function(date) {
			return moment.utc(date).local().format('LLL');
		},
		showRemoveConfirmation: function() {
			this.confirmationShown(true);
		},
		hideRemoveConfirmation: function() {
			this.confirmationShown(false);
		},
		removeSeason: function() {

		}
	});

	return Model;
})(ko, moment);

admin.seasons.index.IndexModel = (function(ko, _) {
	var Model = function(data) {
		this.seasons = ko.observableArray([]);

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.seasons(_.map(data, function(item) {
				return new admin.seasons.index.SeasonModel(item);
			}, this));
		}
	});

	return Model;
})(ko, _);

(function(ko) {
	var model = new admin.seasons.index.IndexModel(admin.seasons.index.data);

	ko.applyBindings(model);
})(ko);