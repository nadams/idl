/* global idl, profile */

(function(ajax, profile) {
  'use strict';

  profile.index.ProfileRepository = (function() {
    var Repository = function() {
      this.becomePlayer = function(url, context) {
        return ajax.post(url, {}, context);
      };

      this.updatePassword = function(url, currentPassword, newPassword, confirmPassword, context) {
        var data = {
          currentPassword: currentPassword,
          newPassword: newPassword,
          confirmPassword: confirmPassword
        };

        return ajax.post(url, data, context);
      };
    };

    return Repository;
  })();

})(idl.ajax, profile);
