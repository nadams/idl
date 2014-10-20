/* global idl, profile */

(function(ajax, profile) {
  'use strict';

  profile.index.ProfileRepository = (function() {
    var Repository = function() {
      this.becomePlayer = function(url, context) {
        return ajax.post(url, {}, context);
      };
    };

    return Repository;
  })();

})(idl.ajax, profile);
