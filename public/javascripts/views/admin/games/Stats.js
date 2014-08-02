/* global ko, admin */

admin.games.stats.StatsModel = (function(ko) {
  'use strict';

  var Model = function(data, routes) {
    this.routes = routes;
    this.seasonId = 0;
    this.gameId = 0;
    this.stats = ko.observable();

    this.initialize(data);

    this.hasStats = ko.computed(function() {
      return this.stats().length > 0;
    }, this);

    this.uploadStatsUrl = ko.computed(function() {
      return this.routes.controllers.GameController.uploadStats(this.seasonId, this.gameId).url;
    }, this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.seasonId = data.seasonId;
      this.gameId = data.gameId;
      this.stats(data.stats);
    },
    statsUploadDone: function(data) {},
    statsUploadFail: function(data) {}
  });

  return Model;
})(ko);

(function(ko, admin) {
  'use strict';

  var model = new admin.games.stats.StatsModel(admin.games.stats.data, admin.games.stats.routes);
  ko.applyBindings(model);
})(ko, admin);