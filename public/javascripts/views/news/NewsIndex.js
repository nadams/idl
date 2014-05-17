/* global ko, moment, _ */

'use strict';
var admin = admin || {};
admin.news = admin.news || {};
admin.news.index = admin.news.index || {};

admin.news.index.NewsIndexModel = (function (ko, _) {
	var Model = function (data) {
		this.newsItems = [];

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function (data) {
			this.newsItems = _.map(data.newsItems, function (item) {
				return new admin.news.index.NewsListItemModel(item);
			});
		}
	});

	return Model;
})(ko, _);

admin.news.index.NewsListItemModel = (function (ko, moment) {
	var Model = function (data) {
		this.newsId = 0;
		this.subject = ko.observable('');
		this.dateModified = ko.observable();
		this.dateCreated = ko.observable();
		this.postedByUsername = ko.observable();
		this.editUrl = '';
		this.removeUrl = '';

		this.initialize(data);

		this.niceDateModified = ko.computed(function() {
			return this.niceDate(this.dateModified());
		}, this);

		this.niceDateCreated = ko.computed(function() {
			return this.niceDate(this.dateCreated());
		}, this);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function (data) {
			this.newsId = data.newsId;
			this.subject(data.subject);
			this.dateModified(data.dateModified);
			this.dateCreated(data.dateCreated);
			this.postedByUsername(data.postedByUsername);
			this.editUrl = data.editUrl;
			this.removeUrl = data.removeUrl;
		},
		niceDate: function (date) {
			return moment.utc(date).local().calendar();
		}
	});

	return Model;
})(ko, moment);

(function (admin, ko) {
	var model = new admin.news.index.NewsIndexModel(admin.news.index.data);

	ko.applyBindings(model);
})(admin, ko);
