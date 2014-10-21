/* global idl, profile, routes */

(function(ajax, profile, routes) {
  'use strict';

  profile.index.ProfileRepository = (function() {
    var Repository = function() {
      this.becomePlayer = function(context) {
        var url = routes.controllers.ProfileController.becomePlayer().url; 
        return ajax.post(url, {}, context);
      };

      this.updatePassword = function(currentPassword, newPassword, confirmPassword, context) {
        var url = routes.controllers.ProfileController.updatePassword().url;
        var data = {
          currentPassword: currentPassword,
          newPassword: newPassword,
          confirmPassword: confirmPassword
        };

        return ajax.post(url, data, context);
      };

      this.addPlayer = function(playerName, context) {
        var url = routes.controllers.ProfileController.addPlayer(playerName).url;
        return ajax.post(url, {}, context);
      };

      this.removePlayer = function(playerId, context) {
        var url = routes.controllers.ProfileController.removePlayer(playerId).url;
        return ajax.post(url, {}, context);
      };

      this.updateDisplayName = function(displayName, context) {
        var url = routes.controllers.ProfileController.updateDisplayName(displayName).url;
        return ajax.post(url, {}, context);
      };
    };

    return Repository;
  })();

})(idl.ajax, profile, routes);
