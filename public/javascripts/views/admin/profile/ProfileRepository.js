/* global routes, idl, admin */

(function(admin, idl, routes) {
  'use strict';
  admin.profile = admin.profile || {};
  admin.profile.ProfileRepository = (function(ajax, routes) {
    var Repository = function() {
      this.searchProfiles = function(name, context) {
        return ajax.get(routes.controllers.AdminProfileController.search(name).url, context);
      };
    };

    return Repository;
  })(idl.ajax, routes);
})(admin, idl, routes);

