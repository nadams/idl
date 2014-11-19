/* global ko, admin */

(function(ko, admin, repository) {
  'use strict';

  admin.profile = admin.profile || {};
  admin.profile.IndexModel = (function(ko) {
    var Model = function(data) {
      this.profileSearchValue = ko.observable().extend({ throttle: 250 });
      this.profileSearchValue.subscribe(function(newValue) {
        var promise = repository.searchProfiles(this.profileSearchValue(), this);
        promise.done(function(data) {
          console.log(data);
        
        });

        promise.fail(function(data) {
        
        });

        promise.always(function(data) {
        
        });
      }, this);
    };

    return Model;
  })(ko);
})(ko, admin, new admin.profile.ProfileRepository());

(function(ko, admin) {
  'use strict';

  var model = new admin.profile.IndexModel({});
  ko.applyBindings(model);
})(ko, admin);
