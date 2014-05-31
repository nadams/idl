/* global ko, moment, _ */
'use strict';

var admin = admin || {};
admin.seasons = admin.seasons || {};
admin.seasons.index = admin.seasons.index || {};

admin.seasons.index.SeasonModel = (function(ko, moment) {
	var Model = function(data, repository) {
		this.repository = repository;

		this.seasonId = 0;
		this.name = '';
		this.startDate = ko.observable();
		this.endDate = ko.observable();
		this.editLink = '';
		this.removeLink = '';
		this.confirmationShown = ko.observable(false);
		this.isRemovingSeason = ko.observable(false);

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
		removeSeason: function(parent) {
			this.isRemovingSeason(true);
			var promise = this.repository.removeSeason(this.seasonId, this);

			promise.always(function() {
				this.isRemovingSeason(false);
				this.confirmationShown(false);
			});

			promise.done(function(data) {
				var seasonToRemove = _.find(parent.seasons(), function(item) {
					return item.seasonId === data;
				});

				parent.seasons.remove(seasonToRemove);
			});

			promise.fail(function(result) {
				parent.errorMessage(result.responseText);
			});
		}
	});

	return Model;
})(ko, moment);

admin.seasons.index.IndexModel = (function(ko, _) {
	var Model = function(data, repository) {
		this.seasons = ko.observableArray([]);
		this.errorMessage = ko.observable('');

		this.initialize(data, repository);

		this.errorMessageShown = ko.computed(function() {
			return this.errorMessage().length > 0;
		}, this);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data, repository) {
			this.seasons(_.map(data, function(item) {
				return new admin.seasons.index.SeasonModel(item, repository);
			}, this));
		},
		clearErrorMessage: function() {
			this.errorMessage('');
		}
	});

	return Model;
})(ko, _);

(function(ko) {
	var model = new admin.seasons.index.IndexModel(admin.seasons.index.data, new admin.seasons.SeasonsRepository());

	ko.applyBindings(model);
})(ko);