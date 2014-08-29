/* global ko, _, moment, jQuery, admin */

admin.games.stats.StatsModel = (function(ko, _, $) {
  'use strict';

  var Model = function(data, routes) {
    this.routes = routes;
    this.seasonId = ko.observable(0);
    this.gameId = ko.observable(0);
    this.statsUploaded = ko.observable(false);
    this.demoInfo = ko.observableArray();
    this.rounds = ko.observableArray();

    this.uploadStatsSuccess = ko.observable();
    this.uploadStatsFailure = ko.observable();
    this.isUploading = ko.observable(false);

    this.initialize(data, routes);

    this.uploadStatsUrl = this.routes.controllers.GameController.uploadStats(this.seasonId(), this.gameId()).url;

    this.statsUploadStart = function() {
      this.isUploading(true);
    }.bind(this);

    this.statsUploadFail = function() {
      this.uploadStatsFailure('Something went wrong when uploading stats.');
    }.bind(this);

    this.statsUploadDone = function(e, data) {
      this.uploadStatsSuccess('Stats uploaded successfully.');
      this.initialize(data.result, this.routes);
    }.bind(this);

    this.statsUploadAlways = function() {
      this.isUploading(false);
    }.bind(this);

    this.removeRound = function(round) {
      var url = this.routes.controllers.GameController.removeRound(this.seasonId(), this.gameId(), round.roundId()).url;
      var promise = $.ajax({
        url: url,
        data: '{}',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json',
        context: this
      });

      promise.done(function() {
        this.rounds.remove(round);
      });

      promise.fail(function() {
        alert('unable to remove round');
      });
    }.bind(this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data, routes) {
      this.seasonId(data.seasonId);
      this.gameId(data.gameId);
      this.statsUploaded(data.statsUploaded);

      this.demoInfo(_.map(data.demoInfo, function(data) {
        return new admin.games.stats.StatsDemoModel(this.seasonId(), this.gameId(), data, routes);
      }, this));

      this.rounds(_.map(data.rounds, function(data) {
        return new admin.games.stats.RoundModel(data, routes);
      }, this));
    }
  });

  return Model;
})(ko, _, jQuery);

admin.games.stats.StatsDemoModel = (function(ko) {
  'use strict';

  var Model = function(seasonId, gameId, data, routes) {
    this.routes = routes;
    this.seasonId = seasonId;
    this.gameId = gameId;
    this.playerId = ko.observable();
    this.playerName = ko.observable();
    this.demo = ko.observable();

    this.isMissingDemoUploadShown = ko.observable(false);
    this.isDemoUploadShown = ko.observable(false);
    this.isUploading = ko.observable(false);

    this.initialize(data);

    this.uploadUrl = this.routes.controllers.GameController.uploadDemo(this.seasonId, this.gameId, this.playerId()).url;

    this.uploadStart = function() {
      this.isUploading(true);
    }.bind(this);

    this.uploadDone = function(e, data) {
      this.initialize(data.result);
    }.bind(this);

    this.uploadAlways = function() {
      this.isUploading(false);
    }.bind(this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.playerId(data.playerId);
      this.playerName(data.playerName);

      if (data.demoData) {
        this.demo(new admin.games.stats.DemoModel(data.demoData));
      }
    },
    showMissingDemoUpload: function() {
      this.isMissingDemoUploadShown(true);
    },
    hideMissingDemoUpload: function() {
      this.isMissingDemoUploadShown(false);
    },
    showDemoUpload: function() {
      this.isDemoUploadShown(true);
    },
    hideDemoUpload: function() {
      this.isDemoUploadShown(false);
    }
  });

  return Model;
})(ko);

admin.games.stats.DemoModel = (function(ko, moment) {
  'use strict';

  var Model = function(data) {
    this.gameDemoId = ko.observable();
    this.filename = ko.observable();
    this.dateUploaded = ko.observable();

    this.initialize(data);

    this.dateUploadedNiceFormat = ko.computed(function() {
      return moment.utc(this.dateUploaded()).fromNow();
    }, this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.gameDemoId(data.gameDemoId);
      this.filename(data.filename);
      this.dateUploaded(data.dateUploaded);
    }
  });

  return Model;
})(ko, moment);

admin.games.stats.RoundModel = (function(ko) {
  'use strict';

  var Model = function(data, routes) {
    this.routes = routes;

    this.roundId = ko.observable();
    this.mapNumber = ko.observable();
    this.playerData = ko.observableArray();

    this.initialize(data);

    this.roundName = ko.computed(function() {
      return this.roundId() + ' - ' + this.mapNumber();
    }, this);

    this.teamStats = ko.computed(function() {
      var teams = _(this.playerData()).groupBy(function(item) {
        return item.teamId();
      }, this).map(function(item) {
        return item;
      }).value();



      return [];
    }, this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.roundId(data.roundId);
      this.mapNumber(data.mapNumber);
      this.playerData(_.map(data.playerData, function(item) {
        return new admin.games.stats.RoundResultModel(item, this.routes);
      }, this));
    }
  });

  return Model;
})(ko);

admin.games.stats.RoundResultModel = (function(ko) {
  'use strict';

  var Model = function(data, routes) {
    this.routes = routes;

    this.roundResultId = ko.observable();
    this.playerId = ko.observable();
    this.playerName = ko.observable();
    this.teamId = ko.observable();
    this.teamName = ko.observable();
    this.captures = ko.observable();
    this.pCaptures = ko.observable();
    this.drops = ko.observable();
    this.frags = ko.observable();
    this.deaths = ko.observable();

    this.initialize(data);

    this.dataIsValid = ko.computed(function() {
      return true;
    }, this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.roundResultId(data.roundResultId);
      this.playerId(data.playerId);
      this.playerName(data.playerName);
      this.teamId(data.teamId);
      this.teamName(data.teamName);
      this.captures(data.captures);
      this.pCaptures(data.pCaptures);
      this.drops(data.drops);
      this.frags(data.frags);
      this.deaths(data.deaths);
    }
  });

  return Model;
})(ko);

(function(ko, admin) {
  'use strict';

  var model = new admin.games.stats.StatsModel(admin.games.stats.data, admin.games.stats.routes);
  ko.applyBindings(model);
})(ko, admin);