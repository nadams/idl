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
    };

    return Repository;
  })(idl.ajax, routes);
})(admin, idl, routes);

