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
            return item.roleId() === role.roleId();
          });
        }, this);
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.profileId(data.profileId);
        this.displayName(data.displayName);
        this.email(data.email);
        this.passwordExpired(data.passwordExpired);
        this.lastLoginDate(data.lastLoginDate);

        this.profileRoles(_.map(data.profileRoles, function(item) {
          return new admin.profile.RoleModel(item);
        }));

        this.allRoles(_.map(data.allRoles, function(item) {
          return new admin.profile.RoleModel(item);
        }));

        this.playerNames(_.map(data.playernames, function(item) {
          return new admin.profile.PlayerNameModel(item);
        }));
      },
      updateProfile: function() {
        var promise = repository.updateProfile(this.profileId(), this.displayName(), this.email(), this.passwordExpired(), this);
        promise.done(function(data) {
          this.displayName(data.displayName);
          this.email(data.email);
          this.passwordExpired(data.passwordExpired);
        });

        promise.fail(function() {});
        promise.always(function() {});

        return false;
      }
    });

    return Model;
  })();

  admin.profile.PlayerNameModel = (function() {
    var Model = function(data) {
      this.playerId = ko.observable();
      this.playerName = ko.observable();
      this.isApproved = ko.observable();

      this.initialize(data);
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
