/* global ko, _, moment, admin */

(function(ko, _, moment, admin, repository) {
  'use strict';

  admin.profile.ProfileModel = (function() {
    var Model = function(data) {
      this.profileId = ko.observable();
      this.displayName = ko.observable();
      this.email = ko.observable();
      this.passwordExpired = ko.observable();
      this.lastLoginDate = ko.observable();
      this.profileRoles = ko.observableArray([]);
      this.playerNames = ko.observableArray([]);
      this.allRoles = ko.observableArray([]);
    
      this.initialize(data);

      this.lastLoginDateFormatted = ko.computed(function() {
        return moment.utc(this.lastLoginDate()).fromNow();
      }, this);

      this.availableRoles = ko.computed(function() {
        var profileRoles = this.profileRoles();

        return _.reject(this.allRoles(), function(role) {
          return _.find(profileRoles, function(item) {
            return item === role;
          });
        }, this);
      }, this);

      this.hasPlayerNames = ko.computed(function() {
        return this.playerNames().length > 0;
      }, this);

      this.canAssignRoles = ko.computed(function() {
        return this.selectedRolesToAssign().length > 0;
      }, this);

      this.canRemoveRoles = ko.computed(function() {
        return this.selectedRolesToRemove().length > 0;
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.profileId(data.profileId);
        this.displayName(data.displayName);
        this.email(data.email);
        this.passwordExpired(data.passwordExpired);
        this.lastLoginDate(data.lastLoginDate);

        this.isUpdatingProfile = ko.observable(false);

        this.selectedRolesToAssign = ko.observableArray([]);
        this.selectedRolesToRemove = ko.observableArray([]);

        this.allRoles(_.map(data.allRoles, function(item) {
          return new admin.profile.RoleModel(item);
        }));

        this.profileRoles(_.map(data.profileRoles, function(item) {
          return _.find(this.allRoles(), function(role) {
            return role.roleId() === item.roleId;
          }, this);
        }, this));

        this.playerNames(_.map(data.playerNames, function(item) {
          return new admin.profile.PlayerNameModel(item);
        }));
      },
      updateProfile: function() {
        this.isUpdatingProfile(true);
        var promise = repository.updateProfile(this.profileId(), this.displayName(), this.email(), this.passwordExpired(), this);
        promise.done(function(data) {
          this.displayName(data.displayName);
          this.email(data.email);
          this.passwordExpired(data.passwordExpired);
        });

        promise.fail(function() {});
        promise.always(function() {
          this.isUpdatingProfile(false);
        });

        return false;
      },
      removeRoles: function() {
        if(this.canRemoveRoles()) {
          var roleIds = _.map(this.selectedRolesToRemove(), function(item) {
            return item.roleId();
          });

          this.selectedRolesToRemove.removeAll();
          
          var promise = repository.removeRoles(this.profileId(), roleIds, this);
          promise.done(function(data) {
            var rolesToRemove = _.filter(this.profileRoles(), function(item) {
              return _.find(data.roleIds, function(roleId) {
                return roleId === item.roleId();
              });
            });

            this.profileRoles.removeAll(rolesToRemove);
          });
        }
      },
      addRoles: function() {
        if(this.canAssignRoles()) {
          var promise = repository.addRoles(this.profileId(), this.selectedRolesToAssign(), this);
          this.selectedRolesToAssign.removeAll();
          promise.done(function(data) {
            var rolesToAdd = _.filter(this.allRoles(), function(item) {
              return _.find(data.roleIds, function(roleId) {
                return roleId === item.roleId();
              });
            });

            _.forEach(rolesToAdd, function(item) {
              this.profileRoles.push(item);
            }, this);
          });
        }
      },
      removePlayer: function(context, player) {
        var promise = repository.removePlayer(context.profileId(), player.playerId(), context);
        promise.done(function(result) {
          this.playerNames.remove(function(item) {
            return item.playerId() === result.playerId;
          });
        });

        promise.fail(function(message) {
        });
      }
    });

    return Model;
  })();

  admin.profile.PlayerNameModel = (function() {
    var Model = function(data) {
      this.playerId = ko.observable();
      this.playerName = ko.observable();
      this.isApproved = ko.observable();

      this.promptRemoveWarning = ko.observable(false);

      this.initialize(data);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.playerId(data.playerId);
        this.playerName(data.playerName);
        this.isApproved(data.isApproved);
      },
      approvePlayer: function(data, profileId) {
        var promise = repository.approvePlayer(profileId, data.playerId(), data);
        promise.done(function(result) {
          if(result.playerId === data.playerId()) {
            data.isApproved(true);
          }
        });

        promise.fail(function(message) {
        });
      },
      unapprovePlayer: function(data, profileId) {
        var promise = repository.unapprovePlayer(profileId, data.playerId(), data);
        promise.done(function(result) {
          if(result.playerId === data.playerId()) {
            data.isApproved(false);
          }
        });

        promise.fail(function(message) {
        });
      },
      showRemovePromptWarning: function() {
        this.promptRemoveWarning(true);
      },
      hideRemovePromptWarning: function() {
        this.promptRemoveWarning(false);
      },
      toggleRemovePromptWarning: function() {
        this.promptRemoveWarning(!this.promptRemoveWarning());
      }
    });

    return Model;
  })();

  admin.profile.RoleModel = (function() {
    var Model = function(data) {
      this.roleId = ko.observable();
      this.roleName = ko.observable();

      this.initialize(data);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.roleId(data.roleId);
        this.roleName(data.roleName);
      }
    });

    return Model;
  })();
})(ko, _, moment, admin, new admin.profile.ProfileRepository());

(function(ko, admin) {
  'use strict';

  var model = new admin.profile.ProfileModel(admin.profile.data);
  ko.applyBindings(model);
})(ko, admin);
