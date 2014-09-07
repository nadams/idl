/* global admin, ko, _ */

admin.games.index.GamesModel = (function(ko, _) {
  'use strict';

  var Model = function(data) {
    this.games = ko.observableArray();

    this.initialize(data);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.games(_.map(data.games, function(item) {
        return new admin.games.index.GameModel(item);
      }));
    }
  });

  return Model;
})(ko, _);

admin.games.index.GameModel = (function(ko) {
  'use strict';

  var Model = function(data) {
    this.team1 = ko.observable();
    this.team2 = ko.observable();
    this.scheduledWeek = ko.observable();
    this.gameStatus = ko.observable();
    this.resultsLink = ko.observable();
    this.editLink = ko.observable();
    this.removeLink = ko.observable();

    this.initialize(data);

    this.canAlterGame = ko.computed(function() {
      return this.gameStatus() !== 'Completed';
    }, this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.team1(data.team1);
      this.team2(data.team2);
      this.scheduledWeek(data.scheduledWeek);
      this.gameStatus(data.gameStatus);
      this.resultsLink(data.resultsLink);
      this.editLink(data.editLink);
      this.removeLink(data.removeLink);
    }
  });

  return Model;
})(ko);

(function(ko) {
  'use strict';

  var model = new admin.games.index.GamesModel(admin.games.index.data);
  ko.applyBindings(model);
})(ko);