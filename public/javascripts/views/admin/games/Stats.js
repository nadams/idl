/* global ko, _, admin */

admin.games.stats.StatsModel = (function(ko, _) {
  'use strict';

  var Model = function(data, routes) {
    this.routes = routes;
    this.seasonId = 0;
    this.gameId = 0;
    this.statsUploaded = ko.observable(false);
    this.stats = ko.observableArray();

    this.uploadStatsSuccess = ko.observable();
    this.uploadStatsFailure = ko.observable();

    this.initialize(data);

    this.uploadStatsUrl = ko.computed(function() {
      return this.routes.controllers.GameController.uploadStats(this.seasonId, this.gameId).url;
    }, this);

    this.statsUploadFail = function() {
      this.uploadStatsFailure('Something went wrong when uploading stats.');
    }.bind(this);

    this.statsUploadDone = function(data) {
      this.uploadStatsSuccess('Stats uploaded successfully.');
    }.bind(this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.seasonId = data.seasonId;
      this.gameId = data.gameId;
      this.statsUploaded(data.statsUploaded);
      this.stats(_.map(data.stats, function(data) {
        return new admin.games.stats.StatsDemoModel(data);
      }));
    }
  });

  return Model;
})(ko, _);

admin.games.stats.StatsDemoModel = (function(ko) {
  'use strict';

  var Model = function(data) {
    this.playerId = 0;
    this.playerName = '';
    this.demo = ko.observable();

    this.initialize(data);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.playerId = data.playerId;
      this.playerName = data.playerName;

      if (data.demoData) {
        this.demo(new admin.games.stats.DemoModel(data.demoData));
      }
    }
  });

  return Model;
})(ko);

admin.games.stats.DemoModel = (function(ko) {
  'use strict';

  var Model = function(data) {
    this.gameDemoId = ko.observable();
    this.filename = ko.observable();
    this.dateUploaded = ko.observable();

    this.initialize(data);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.gameDemoId(data.gameDemoId);
      this.filename(data.filename);
      this.dateUploaded(data.filename);
    }
  });

  return Model;
})(ko);

(function(ko, admin) {
  'use strict';

  var model = new admin.games.stats.StatsModel(admin.games.stats.data, admin.games.stats.routes);
  ko.applyBindings(model);
})(ko, admin);