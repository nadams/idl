/* global routes, idl, admin */

(function(admin, idl, routes) {
  'use strict';
  admin.profile = admin.profile || {};
  admin.profile.ProfileRepository = (function(ajax, routes) {
    var Repository = function() {
      this.searchProfiles = function(name, context) {
        return ajax.get(routes.controllers.AdminProfileController.search(name).url, context);
      };

      this.updateProfile = function(profileId, displayName, email, passwordExpired, context) {
        var url = routes.controllers.AdminProfileController.updateProfile(profileId).url;
        var data = {
          profileId: profileId,
          displayName: displayName,
          email: email,
          passwordExpired: passwordExpired
        };

        return ajax.post(url, data, context);
      };

      this.removeRoles = function(profileId, roles, context) {
        var url = routes.controllers.AdminProfileController.removeRoles(profileId).url;
        var data = {
          roleIds: roles
        };

        return ajax.post(url, data, context);
      };

      this.addRoles = function(profileId, roles, context) {
        var url = routes.controllers.AdminProfileController.addRoles(profileId).url;
        var data = {
          roleIds: roles
        };

        return ajax.post(url, data, context);
      };

      this.unapprovePlayer = function(playerId, context) {
        var url = routes.controllers.AdminProfileController.unapprovePlayer(playerId).url;
        return ajax.post(url, {}, context);
      };

      this.approvePlayer = function(playerId, context) {
        var url = routes.controllers.AdminProfileController.approvePlayer(playerId).url;
        return ajax.post(url, {}, context);
      };

      this.removePlayer = function(playerId, context) {
        var url = routes.controllers.AdminProfileController.removePlayer(playerId).url;
        return ajax.post(url, {}, context);
      };
    };

    return Repository;
  })(idl.ajax, routes);
})(admin, idl, routes);

