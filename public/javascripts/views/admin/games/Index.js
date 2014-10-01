/* global admin, ko, _ */

admin.games.index.GamesModel = (function(ko, _) {
  'use strict';

  var Model = function(data) {
    this.games = ko.observableArray();

    this.team1SortOrder = ko.observable(true);
    this.team2SortOrder = ko.observable(true);
    this.weekSortOrder = ko.observable(true);
    this.statusSortOrder = ko.observable(true);
    this.gameTypeSortOrder = ko.observable(true);

    this.team1CurrentSort = ko.observable(false);
    this.team2CurrentSort = ko.observable(false);
    this.weekCurrentSort = ko.observable(false);
    this.statusCurrentSort = ko.observable(false);
    this.gameTypeCurrentSort = ko.observable(false);

    this.initialize(data);

    this.sortByWeek();
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.games(_.map(data.games, function(item) {
        return new admin.games.index.GameModel(item);
      }));
    },
    sortByTeam1: function() {
      var that = this;

      that.games.sort(function(left, right) {
        if(that.team1SortOrder()) {
          return left.team1() > right.team1() ? 1 : -1;
        } else {
          return left.team1() < right.team1() ? 1 : -1;
        }
      });

      that.team1CurrentSort(true);
      that.team2CurrentSort(false);
      that.weekCurrentSort(false);
      that.statusCurrentSort(false);
      that.gameTypeCurrentSort(false);
      that.team1SortOrder(!that.team1SortOrder());
    },
    sortByTeam2: function() {
      var that = this;

      that.games.sort(function(left, right) {
        if(that.team2SortOrder()) {
          return left.team2() > right.team2() ? 1 : -1;
        } else {
          return left.team2() < right.team2() ? 1 : -1;
        }
      });

      that.team1CurrentSort(false);
      that.team2CurrentSort(true);
      that.weekCurrentSort(false);
      that.statusCurrentSort(false);
      that.gameTypeCurrentSort(false);
      that.team2SortOrder(!that.team2SortOrder());
    },
    sortByWeek: function() {
      var that = this;

      that.games.sort(function(left, right) {
        if(that.weekSortOrder()) {
          return left.scheduledWeek() > right.scheduledWeek() ? 1 : -1;
        } else {
          return left.scheduledWeek() < right.scheduledWeek() ? 1 : -1;
        }
      });

      that.team1CurrentSort(false);
      that.team2CurrentSort(false);
      that.weekCurrentSort(true);
      that.statusCurrentSort(false);
      that.gameTypeCurrentSort(false);
      that.weekSortOrder(!that.weekSortOrder());
    },
    sortByStatus: function() {
      var that = this;

      that.games.sort(function(left, right) {
        if(that.statusSortOrder()) {
          return left.gameStatus() > right.gameStatus() ? 1 : -1;
        } else {
          return left.gameStatus() < right.gameStatus() ? 1 : -1;
        }
      });

      that.team1CurrentSort(false);
      that.team2CurrentSort(false);
      that.weekCurrentSort(false);
      that.statusCurrentSort(true);
      that.gameTypeCurrentSort(false);
      that.statusSortOrder(!that.statusSortOrder());
    },
    sortByGameType: function() {
      var that = this;

      that.games.sort(function(left, right) {
        if(that.gameTypeSortOrder()) {
          return left.gameType() > right.gameType() ? 1 : -1;
        } else {
          return left.gameType() < right.gameType() ? 1 : -1;
        }
      });

      that.team1CurrentSort(false);
      that.team2CurrentSort(false);
      that.weekCurrentSort(false);
      that.statusCurrentSort(false);
      that.gameTypeCurrentSort(true);
      that.gameTypeSortOrder(!that.gameTypeSortOrder());
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
    this.gameType = ko.observable();
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
      this.gameType(data.gameType);
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
