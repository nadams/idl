/* global ko, moment */
'use strict';

(function(ko, moment) {
	var Model = function(data) {
		this.seasonId = 0;
		this.name = '';
		this.startDate = '';
		this.endDate = '';

		this.initialize(data);

		this.niceStartDate = ko.computed(function() {
			return this.niceDate(this.startDate);
		}, this);

		this.niceEndDate = ko.computed(function() {
			return this.niceDate(this.endDate);
		}, this);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.seasonId = data.seasonId;
			this.name = data.name;
			this.startDate = data.startDate;
			this.endDate = data.endDate;
		},
		niceDate: function(date) {
			return moment.utc(date).local().calendar();
		}
	});

	return Model;
})(ko, moment);