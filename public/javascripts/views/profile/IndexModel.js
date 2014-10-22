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
        });

        promise.fail(function(data) {
          this.profileIsNowPlayer(false);
        });

        promise.always(function(data) {
          console.log(data);
          this.profileIsNowPlayerMessage(data);
        });
      }
    });

    return Model;
  })();

  profile.index.ProfileModel = (function() {
    var Model = function(data) {
      this.displayName = ko.observable();
      this.currentPassword = ko.observable('');
      this.newPassword = ko.observable('');
      this.confirmPassword = ko.observable('');
      this.successfullyUpdatedPassword = ko.observable(false);
      this.successfullyUpdatedDisplayName = ko.observable(true);
      
      this.currentPasswordError = ko.observable('');
      this.newPasswordError = ko.observable('');
      this.confirmPasswordError = ko.observable('');
      this.globalErrors = ko.observableArray([]);

      this.isUpdatingDisplayName = ko.observable(false);

      this.initialize(data);

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
          this.newPasswordError(data.currentPasswordError);
          this.confirmPasswordError(data.confirmPasswordError);
          this.globalErrors.removeAll();
          this.globalErrors(data.globalErrors);
        });
      },
      updateDisplayName: function() {
        this.isUpdatingDisplayName(true);

        var displayName = this.displayName();
        var promise = repository.updateDisplayName(displayName, this);

        promise.done(function() {
          console.log('success');
        });

        promise.always(function() {
          this.isUpdatingDisplayName(false);
        });

        promise.fail(function() {
          console.log('failure');
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

  var model = new profile.index.IndexModel(profile.index.data);
  ko.applyBindings(model);
})(ko, _, profile, new profile.index.ProfileRepository());
