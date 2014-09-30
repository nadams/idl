/* global ko, _, brackets */

brackets.index.IndexModel = (function(ko, _) {
  'use strict';

  var Model = function(data) {
    this.groupByWeek = function() {
      return this.collectionUtils.multiGroup(this.playoffStats(), function(item) {
        return item.weekId();
      });
    };

    this.playoffStats = ko.observableArray([]);
    this.regularSeasonStats = ko.observableArray([]);

    this.initialize(data);

    this.hasPlayoffStats = ko.computed(function() {
      return this.playoffStats().length > 0;
    }, this);

    this.hasRegularSeasonStats = ko.computed(function() {
      return this.regularSeasonStats().length > 0;
    }, this);
    
    this.bracketStats = ko.computed(function() {
      return {
        results: [],
        teams: []
      };
    }, this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.playoffStats(_.map(data.playoffStats, function(item) {
        return new brackets.index.PlayoffGameStatsModel(item);
      }, this));
      
      this.regularSeasonStats(_.map(data.regularStats, function(item) {
        return new brackets.index.RegularSeasonStatsModel(item);
      }, this));
    }
  });

  return Model;
})(ko, _);

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

brackets.index.PlayoffGameStatsModel = (function(ko) {
  'use strict';

  var Model = function(data) {
    this.gameId = ko.observable();
    this.teamStats = ko.observableArray([]);
    this.initialize(data);
  };
  
  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.gameId(data.gameId);
      this.teamStats(_.map(data.teamStats, function(item) {
        return new brackets.index.PlayoffTeamStatsModel(item);
      }, this));
    }
  });
  
  return Model;
})(ko);

brackets.index.PlayoffTeamStatsModel = (function(ko) {
  'use strict';
  
  var Model = function(data) {
    this.teamId = ko.observable();
    this.teamName = ko.observable();
    this.wins = ko.observable();
    
    this.initialize(data);
  };
  
  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.teamId(data.teamId);
      this.teamName(data.teamName);
      this.wins(data.wins);
    }
  });
  
  return Model;
})(ko);

(function(ko, brackets) {
  'use strict';

  var model = new brackets.index.IndexModel(brackets.index.data);
  ko.applyBindings(model);
})(ko, brackets);
