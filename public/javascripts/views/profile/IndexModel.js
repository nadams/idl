/* global ko, profile, _ */

(function(ko, _, profile, repository) {
  'use strict';

  var nullOrEmpty = function(string) {
    return typeof string === 'undefined' || string.length === 0;
  };

  profile.index.IndexModel = (function() {
    var Model = function(data) {
      this.profileId = ko.observable();
      this.profileIsPlayer = ko.observable(false);
      this.profileModel = new profile.index.ProfileModel(data.profileModel);
      this.playerModel = new profile.index.PlayerModel(data.playerModel);
      this.teamsModel = new profile.index.TeamsModel(data.teams, this.playerModel);

      this.profileIsNowPlayer = ko.observable(false);
      this.profileIsNowPlayerMessage = ko.observable('');

      this.initialize(data);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.profileId(data.profileId);
        this.profileIsPlayer(data.profileIsPlayer);
      },
      makeProfilePlayer: function() {
        var promise = repository.becomePlayer(this);

        promise.success(function(data) {
          this.profileIsNowPlayer(true);
          this.profileIsPlayer(true);
          this.playerModel.manuallyAddPlayerName(data.playerResult);
        });

        promise.fail(function(data) {
          this.profileIsNowPlayer(false);
        });

        promise.always(function(data) {
          this.profileIsNowPlayerMessage(data.resultMessage);
        });
      }
    });

    return Model;
  })();

  profile.index.ProfileModel = (function() {
    var Model = function(data) {
      this.displayName = ko.observable();
      this.displayNameInput = ko.observable();
      this.currentPassword = ko.observable('');
      this.newPassword = ko.observable('');
      this.confirmPassword = ko.observable('');
      this.successfullyUpdatedPassword = ko.observable(false);
      this.successfullyUpdatedDisplayName = ko.observable(false);
      
      this.displayNameError = ko.observable();
      this.currentPasswordError = ko.observable('');
      this.newPasswordError = ko.observable('');
      this.confirmPasswordError = ko.observable('');
      this.globalErrors = ko.observableArray([]);

      this.isUpdatingDisplayName = ko.observable(false);

      this.initialize(data);

      this.canUpdateDisplayName = ko.computed(function() {
        return !nullOrEmpty(this.displayNameInput()) && this.displayNameInput() !== this.displayName();
      }, this);

      this.hasDisplayNameError = ko.computed(function() {
        return !nullOrEmpty(this.displayNameError());
      }, this);

      this.hasGlobalErrors = ko.computed(function() {
        return this.globalErrors().length > 0;
      }, this);
      
      this.hasCurrentPasswordError = ko.computed(function() {
        return !nullOrEmpty(this.currentPasswordError());
      }, this);
      
      this.hasNewPasswordError = ko.computed(function() {
        return !nullOrEmpty(this.newPasswordError());
      }, this);

      this.hasConfirmPasswordError = ko.computed(function() {
        return !nullOrEmpty(this.confirmPasswordError());
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.displayName(data.displayName);
        this.displayNameInput(data.displayName);
      },
      updatePassword: function() {
        var promise = repository.updatePassword(
          this.currentPassword(),
          this.newPassword(),
          this.confirmPassword(),
          this
        );

        promise.done(function() {
          this.clearPasswordForm();
          this.successfullyUpdatedPassword(true);
          this.globalErrors.removeAll();
        });

        promise.fail(function(response) {
          var data = response.responseJSON;

          this.successfullyUpdatedPassword(false);
          this.currentPasswordError(data.currentPasswordError);
          this.newPasswordError(data.newPasswordError);
          this.confirmPasswordError(data.confirmPasswordError);
          this.globalErrors.removeAll();
          this.globalErrors(data.globalErrors);
        });
      },
      updateDisplayName: function() {
        this.isUpdatingDisplayName(true);

        var displayName = this.displayNameInput();
        var promise = repository.updateDisplayName(displayName, this);

        promise.done(function() {
          this.successfullyUpdatedDisplayName(true);
          this.displayNameError('');
          this.displayName(this.displayNameInput());
        });

        promise.always(function() {
          this.isUpdatingDisplayName(false);
        });

        promise.fail(function(error) {
          this.successfullyUpdatedDisplayName(false);
          this.displayNameError(error.responseText);
        });
      },
      clearPasswordForm: function() {
        this.currentPassword('');
        this.newPassword('');
        this.confirmPassword('');
        this.currentPasswordError('');
        this.newPasswordError('');
        this.confirmPasswordError('');
        this.globalErrors.removeAll();
      }
    });

    return Model;
  })();

  profile.index.PlayerModel = (function() {
    var Model = function(data) {
      this.playerNames = ko.observableArray();
      this.playerNameToCreate = ko.observable('');
      this.isAddingPlayerName = ko.observable(false);
      this.playerNameError = ko.observable();
    
      this.initialize(data);

      this.hasPlayerNameError = ko.computed(function() {
        return !nullOrEmpty(this.playerNameError());
      }, this);

      this.removePlayerName = function(player) {
        var promise = repository.removePlayer(player.playerId(), this);

        promise.done(function(data) {
          this.playerNames.removeAll(_.filter(this.playerNames(), function(item) {
            return item.playerId() === data;
          }, this));
        });
      }.bind(this);
      
      this.canAddPlayerName = ko.computed(function() {
        return this.playerNameToCreate().length > 0;
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.playerNames(_.map(data.playerNames, function(item) {
          return new profile.index.PlayerNameModel(item);
        }, this));
      },
      addPlayerName: function() {
        this.isAddingPlayerName(true);
        this.playerNameError('');
        var playerName = this.playerNameToCreate();
        var promise = repository.addPlayer(playerName, this);

        promise.done(function(data) {
          this.playerNames.push(new profile.index.PlayerNameModel(data));
        });

        promise.always(function() {
          this.isAddingPlayerName(false);
          this.playerNameToCreate('');
        });

        promise.fail(function(error) {
          this.playerNameError(error.responseText);
        });
      },
      manuallyAddPlayerName: function(name) {
        this.playerNames.push(new profile.index.PlayerNameModel(name));
      }
    });

    return Model;
  })();

  profile.index.PlayerNameModel = (function() {
    var Model = function(data) {
      this.playerId = ko.observable();
      this.playerName = ko.observable();
      this.isApproved = ko.observable(false);
      this.isConfirmingRemoval = ko.observable(false);
    
      this.initialize(data);

      this.itemTitle = ko.computed(function() {
        return this.isApproved() ? 'Approved' : 'Needs Approval';
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.playerId(data.playerId);
        this.playerName(data.playerName);
        this.isApproved(data.isApproved);
      },
      toggleConfirmDelete: function() {
        this.isConfirmingRemoval(!this.isConfirmingRemoval());
      }
    });

    return Model;
  })();

  profile.index.TeamsModel = (function() {
    var Model = function(data, playerModel) {
      this.playerModel = playerModel;
      this.teams = ko.observableArray();
      this.selectedTeam = ko.observable();
      this.teamToJoin = ko.observable();
      this.selectedPlayer = ko.observable();
      this.teamJoinError = ko.observable();
      this.teamJoinSuccess = ko.observable();
      
      this.initialize(data);

      this.hasTeams = ko.computed(function() {
        return this.teams().length > 0;
      }, this);
      
      this.firstTeam = ko.computed(function() {
        return this.teams()[0];
      }, this);
      
      this.switchTeam = function(team) {
        this.selectedTeam(team);
      }.bind(this);

      this.enrolledTeams = ko.computed(function() {
        return _.filter(this.teams(), function(item) {
          return item.isApproved();
        });
      }, this);

      this.pendingTeams = ko.computed(function() {
        return _.filter(this.teams(), function(item) {
          return !item.isApproved();
        });
      }, this);

      this.hasEnrolledTeams = ko.computed(function() {
        return this.enrolledTeams().length > 0;
      }, this);

      this.hasPendingTeams = ko.computed(function() {
        return this.pendingTeams().length > 0;
      }, this);

      this.chosenTeamName = ko.computed(function() {
        var selectedTeam = this.selectedTeam();

        return typeof selectedTeam === 'undefined' ? 'Choose...' : selectedTeam.teamName();
      }, this);

      this.hasTeamJoinError = ko.computed(function() {
        return !nullOrEmpty(this.teamJoinError());
      }, this);

      this.hasTeamJoinSuccess = ko.computed(function() {
        return !nullOrEmpty(this.teamJoinSuccess());
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.teams(_.map(data, function(item) {
          return new profile.index.TeamMembershipModel(item);
        }, this));

        if(this.teams().length > 0) {
          this.selectedTeam(this.teams()[0]);
        }
      },
      requestToJoinTeam: function() {
        var playerId = this.selectedPlayer().playerId();
        var teamName = this.teamToJoin();
        var promise = repository.requestToJoinTeam(playerId, teamName, this);

        promise.done(function(data) {
          console.log(data);
        
        });

        promise.fail(function(data) {
          this.teamJoinError(data.responseText);
        });

        promise.always(function() {
        
        });
      }
    });

    return Model;
  })();

  profile.index.TeamMembershipModel = (function() {
    var Model = function(data) {
      this.teamId = ko.observable();
      this.teamName = ko.observable();
      this.members = ko.observableArray();
      this.isApproved = ko.observable();
    
      this.initialize(data);

      this.membersOrdered = ko.computed(function() {
        return _.sortBy(this.members(), function(item) { return item.playerName(); });
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.teamId(data.teamId);
        this.teamName(data.teamName);
        this.isApproved(data.isApproved);
        this.members(_.map(data.members, function(item) {
          return new profile.index.TeamPlayerModel(item);
        }, this));
      }
    });

    return Model;
  })();

  profile.index.TeamPlayerModel = (function() {
    var Model = function(data) {
      this.playerId = ko.observable();
      this.playerName = ko.observable();
      this.isCaptain = ko.observable();
    
      this.initialize(data);

      this.formattedName = ko.computed(function() {
        return this.isCaptain() ? this.playerName() + ' - Team Captain' : this.playerName();
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.playerId(data.playerId);
        this.playerName(data.playerName);
        this.isCaptain(data.isCaptain);
      }
    });

    return Model;
  })();

  var model = new profile.index.IndexModel(profile.index.data);
  ko.applyBindings(model);
})(ko, _, profile, new profile.index.ProfileRepository());
