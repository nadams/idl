/* global $, ko, moment */

'use strict';
var admin = admin || {};
admin.news = admin.news || {};
admin.news.edit = admin.news.edit || {};

admin.news.edit.EditModel = (function(ko) {
	var Model = function(data) {

		this.newsId = 0;
		this.subject = ko.observable('');
		this.content = ko.observable('');

		this.initialize(data);
	};

	ko.utils.extend(Model.prototype, {
		initialize: function(data) {
			this.newsId = data.newsId;
			this.subject(data.subject);
			this.content(data.content);
		}
	});

	return Model;
})(ko, moment);

(function($, ko, admin) {
	var model = new admin.news.edit.EditModel(admin.news.edit.data);
	ko.applyBindings(model);
})($, ko, admin);