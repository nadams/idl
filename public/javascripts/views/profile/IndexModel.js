/* global ko, profile, _ */

(function(ko, _, profile, repository) {
  'use strict';

  profile.index.IndexModel = (function() {
    var Model = function(data) {
      this.profileId = ko.observable();
      this.profileIsPlayer = ko.observable(false);
      this.passwordModel = new profile.index.PasswordModel();
      this.playerModel = new profile.index.PlayerModel(data);

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

  profile.index.PasswordModel = (function() {
    var Model = function(data) {
      this.currentPassword = ko.observable('');
      this.newPassword = ko.observable('');
      this.confirmPassword = ko.observable('');
      this.successfullyUpdatedPassword = ko.observable(false);
      
      this.currentPasswordError = ko.observable('');
      this.newPasswordError = ko.observable('');
      this.confirmPasswordError = ko.observable('');
      
      this.globalErrors = ko.observableArray([]);

      this.hasGlobalErrors = ko.computed(function() {
        return this.globalErrors().length > 0;
      }, this);
      
      this.hasCurrentPasswordError = ko.computed(function() {
        return !this.nullOrEmpty(this.currentPasswordError());
      }, this);
      
      this.hasNewPasswordError = ko.computed(function() {
        return !this.nullOrEmpty(this.newPasswordError());
      }, this);

      this.hasConfirmPasswordError = ko.computed(function() {
        return !this.nullOrEmpty(this.confirmPasswordError());
      }, this);
    };

    ko.utils.extend(Model.prototype, {
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
      clearPasswordForm: function() {
        this.currentPassword('');
        this.newPassword('');
        this.confirmPassword('');
        this.currentPasswordError('');
        this.newPasswordError('');
        this.confirmPasswordError('');
        this.globalErrors.removeAll();
      },
      nullOrEmpty: function(string) {
        return typeof string === 'undefined' || string.length === 0;
      }
    });

    return Model;
  })();

  profile.index.PlayerModel = (function() {
    var Model = function(data) {
      this.playerNames = ko.observableArray();
      this.playerNameToCreate = ko.observable('');
      this.isAddingPlayerName = ko.observable(false);
    
      this.initialize(data);

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
        var playerName = this.playerNameToCreate();
        var promise = repository.addPlayer(playerName, this);

        promise.done(function(data) {
          this.playerNames.push(new profile.index.PlayerNameModel(data));
        });

        promise.always(function() {
          this.isAddingPlayerName(false);
          this.playerNameToCreate('');
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
      }
    });

    return Model;
  })();

  var model = new profile.index.IndexModel(profile.index.data);
  ko.applyBindings(model);
})(ko, _, profile, new profile.index.ProfileRepository());
