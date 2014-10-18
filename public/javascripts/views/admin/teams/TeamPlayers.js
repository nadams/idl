/* global ko, _ */

(function() {
  'use strict';
  
  var admin = admin || {};
  admin.teams = admin.teams || {};
  admin.teams.index = admin.teams.index || {};
})();

admin.teams.index.IndexModel = (function(ko, _) {
  'use strict';
  
  var Model = function(data, repository) {
    this.repository = repository;

    this.availableSeasons = [];
    this.selectedSeason = ko.observable();

    this.availableTeams = ko.observableArray([]);
    this.selectedTeam = ko.observable();

    this.availablePlayers = ko.observableArray([]);
    this.selectedTeamPlayers = ko.observableArray([]);
    this.selectedAvailablePlayers = ko.observableArray([]);

    this.isLoadingTeams = ko.observable(false);
    this.isLoadingPlayers = ko.observable(false);
    this.isAssigningPlayersToTeam = ko.observable(false);
    this.isRemovingPlayersFromTeam = ko.observable(false);
    this.playersInTeamFilter = ko.observable('');
    this.playersAvailableFilter = ko.observable('');
    this.isMakingCaptain = ko.observable(false);

    this.initialize(data);

    this.unassignedPlayers = ko.computed(function() {
      var filter = this.playersAvailableFilter().toUpperCase();

      return _(this.availablePlayers()).filter(function(item) {
        return _.filter(item.teamIds(), function(item) {
          return item.teamId() === this.selectedTeam().teamId;
        }, this).length === 0;
      }, this).filter(function(item) {
        return filter === '' || item.playerName.toUpperCase().indexOf(filter) > -1;
      }).sortBy(function(item) {
        return item.playerName;
      }).value();
    }, this);

    this.playersInCurrentTeam = ko.computed(function() {
      var that = this;
      var filter = this.playersInTeamFilter().toUpperCase();

      return _(this.availablePlayers()).filter(function(item) {
        return _.filter(item.teamIds(), function(item) { 
          return item.teamId() === this.selectedTeam().teamId;
        }, this).length > 0;
      }, this).sort(function(left, right) {
        return that.sortStrings(right.playerName, left.playerName);
      }).filter(function(item) {
        return filter === '' || item.playerName.toUpperCase().indexOf(filter) > -1;
      }).sortBy(function(item) {
        return item.playerName;
      }).value();
    }, this);

    this.canAssignPlayers = ko.computed(function() {
      return this.selectedAvailablePlayers().length > 0 && !this.isAssigningPlayersToTeam();
    }, this);

    this.canRemovePlayers = ko.computed(function() {
      return this.selectedTeamPlayers().length > 0 && !this.isRemovingPlayersFromTeam();
    }, this);

    this.teamCaptain = ko.computed(function() {
      var captain = _.find(this.playersInCurrentTeam(), function(item) {
        return _.any(item.teamIds(), function(item) {
          return item.teamId() === this.selectedTeam().teamId && item.isCaptain();
        }, this);
      }, this);
      
      return typeof captain !== 'undefined' ? captain.playerName : 'No Captain';
    }, this);

    this.selectedSeason.subscribe(function(newSeason) {
      this.availableTeams.removeAll();
      this.selectedTeam(undefined);

      if (this.selectedSeason()) {
        this.isLoadingTeams(true);

        var promise = this.repository.getTeamList(newSeason.seasonId, this);

        promise.done(function(data) {
          var availableTeams = this.availableTeams();

          _.each(data.teams, function(item) {
            availableTeams.push(new admin.teams.index.TeamModel(item));
          }, this);

          this.availableTeams.valueHasMutated();
        });

        promise.always(function() {
          this.isLoadingTeams(false);
        });
      }
    }, this);

    this.selectedTeam.subscribe(function() {
      this.availablePlayers.removeAll();
      this.selectedTeamPlayers.removeAll();
      this.selectedAvailablePlayers.removeAll();

      if (this.selectedTeam()) {
        this.isLoadingPlayers(true);

        var promise = this.repository.getPlayers(this);
        promise.done(function(data) {
          var availablePlayers = this.availablePlayers();

          _.each(data.players, function(item) {
            availablePlayers.push(new admin.teams.index.PlayerModel(item));
          }, this);

          this.availablePlayers.valueHasMutated();
        });

        promise.always(function() {
          this.isLoadingPlayers(false);
        });
      }
    }, this);
    
    this.canMakeCaptain = ko.computed(function() {
      return this.selectedTeamPlayers().length === 1;
    }, this);
    
    this.playerIsCaptain = function(option, player) {
      var playerIsCaptain = _.filter(player.teamIds(), function(teamInfo) { 
        return teamInfo.teamId() === this.selectedTeam().teamId && teamInfo.isCaptain(); 
      }, this).length > 0;
       
      if(playerIsCaptain) {
        option.innerHTML = '* ' + option.innerHTML;
      }
    }.bind(this);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.availableSeasons = _.map(data.seasons, function(seasonData) {
        return new admin.teams.index.SeasonModel(seasonData);
      }, this);
    },
    assignPlayersToCurrentTeam: function() {
      this.isAssigningPlayersToTeam(true);
      var selectedTeamId = this.selectedTeam().teamId,
        selectedPlayerIds = _.map(this.selectedAvailablePlayers(), function(player) {
          return player.playerId;
        }, this);

      var promise = this.repository.assignPlayersToTeam(selectedTeamId, selectedPlayerIds, this);

      promise.done(function(assignedPlayerIds) {
        var assignedPlayers = _.filter(this.availablePlayers(), function(item) {
          return _.contains(assignedPlayerIds, item.playerId);
        }, this);

        var currentTeamId = this.selectedTeam().teamId;

        _.forEach(assignedPlayers, function(player) {
          player.teamIds.push(new admin.teams.index.TeamInfoModel({ teamId: currentTeamId, isCaptain: false }));
        }, this);
      });

      promise.always(function() {
        this.isAssigningPlayersToTeam(false);
      });
    },
    removePlayersFromCurrentTeam: function() {
      this.isRemovingPlayersFromTeam(true);

      var selectedTeamId = this.selectedTeam().teamId,
          selectedPlayerIds = _.map(this.selectedTeamPlayers(), function(player) {
            return player.playerId;
          }, this);

      var promise = this.repository.removePlayersFromTeam(selectedTeamId, selectedPlayerIds, this);

      promise.done(function(assignedPlayerIds) {
        var assignedPlayers = _.filter(this.availablePlayers(), function(item) {
          return _.contains(assignedPlayerIds, item.playerId);
        }, this);

        _.forEach(assignedPlayers, function(player) {
          player.teamIds(_.remove(player.teamIds(), selectedTeamId));
        }, this);
      });

      promise.always(function() {
        this.isRemovingPlayersFromTeam(false);
      });
    },
    sortStrings: function(lName, rName) {
      lName = lName.toUpperCase();
      rName = rName.toUpperCase();
      return lName === rName ? 0 : (lName < rName ? -1 : 1);
    },
    clearPlayersInTeamFilter: function() {
      this.playersInTeamFilter('');
    },
    clearPlayersAvailableFilter: function() {
      this.playersAvailableFilter('');
    },
    makeCaptain: function() {
      var selectedPlayers = this.selectedTeamPlayers();
      if(this.canMakeCaptain()) {
        this.isMakingCaptain(true);
        var player = selectedPlayers[0];
        
        var promise = this.repository.makeCaptain(this.selectedTeam().teamId, player.playerId, this);
        promise.done(function(data) {
          _.forEach(this.playersInCurrentTeam(), function(item) {
            var isSelectedPlayer = item.playerId === player.playerId;
            
            _.forEach(item.teamIds(), function(item) {
              if(isSelectedPlayer && item.teamId() === this.selectedTeam().teamId) {
                item.isCaptain(true);
              } else {
                item.isCaptain(false);
              }
            }, this);
          }, this);
        });
        
        promise.always(function() {
          this.isMakingCaptain(false);
        });
      }
    }
  });

  return Model;
})(ko, _);

admin.teams.index.PlayerModel = (function(ko, _) {
  var Model = function(data) {
    this.playerId = 0;
    this.playerName = '';
    this.teamIds = ko.observableArray([]);

    this.initialize(data);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.playerId = data.playerId;
      this.playerName = data.playerName;
      this.teamIds(_.map(data.teamIds, function(item) {
        return new admin.teams.index.TeamInfoModel(item);
      }));
    }
  });

  return Model;
})(ko, _);

admin.teams.index.TeamInfoModel = (function(ko) {
  var Model = function(data) {
    this.teamId = ko.observable();
    this.isCaptain = ko.observable();
    
    this.initialize(data);
  };
  
  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.teamId(data.teamId);
      this.isCaptain(data.isCaptain);
    }
  });
  
  return Model;
})(ko);

admin.teams.index.TeamModel = (function(ko) {
  var Model = function(data) {
    this.teamId = 0;
    this.teamName = '';

    this.initialize(data);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.teamId = data.teamId;
      this.teamName = data.teamName;
    }
  });

  return Model;
})(ko);

admin.teams.index.SeasonModel = (function(ko) {
  var Model = function(data) {
    this.seasonId = 0;
    this.seasonName = '';

    this.initialize(data);
  };

  ko.utils.extend(Model.prototype, {
    initialize: function(data) {
      this.seasonId = data.seasonId;
      this.seasonName = data.seasonName;
    }
  });

  return Model;
})(ko);

(function(ko) {
  var model = new admin.teams.index.IndexModel(admin.teams.index.data, new admin.teams.TeamRepository());

  ko.applyBindings(model);
})(ko);
