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
    
    this.bracketStats = ko.computed(function() {
      var stats = this.playoffStats();
      var minWeek = _.min(stats, function(item) { return item.weekId(); });
      var teams = [];
      var results = [];
      
      if(typeof minWeek !== 'undefined') { 
        teams = _(stats)
          .filter(function(item) { return item.weekId() === minWeek.weekId(); })
          .map(function(item) { return _.map(item.teamStats(), function(teamStats) { return teamStats.teamName(); }); })
          .value();
        
        results = _(stats)
          .groupBy(function(item) { return item.weekId(); })
          .map(function(item) { 
            return item.map(function(item) { 
              return item.teamStats().map(function(item) { 
                return item.wins(); 
              }); 
            });
          })
          .toArray()
          .value(); 
      }

      return {
        results: results,
        teams: teams
      };
    }, this);
    
    this.hasPlayoffStats = ko.computed(function() {
      var stats = this.bracketStats();
      return stats.results.length > 0 && stats.teams.length > 0;
    }, this);

    this.hasRegularSeasonStats = ko.computed(function() {
      return this.regularSeasonStats().length > 0;
    }, this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.playoffStats(_.map(data.playoffStats.results, function(item) {
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
    this.weekId = ko.observable();
    this.teamStats = ko.observableArray([]);
    this.initialize(data);
  };
  
  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.gameId(data.gameId);
      this.weekId(data.weekId);
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
