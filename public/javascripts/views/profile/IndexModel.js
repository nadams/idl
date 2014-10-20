/* global ko, profile */

(function(ko, profile, repository) {
  'use strict';

  profile.index.IndexModel = (function() {
    var Model = function(data) {
      this.profileId = ko.observable();
      this.profileIsPlayer = ko.observable(false);
      this.passwordModel = new profile.index.PasswordModel();

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
    };

    ko.utils.extend(Model.prototype, {
      updatePassword: function() {
        var promise = repository.updatePassword(
          this.currentPassword(),
          this.newPassword(),
          this.confirmPassword(),
          this
        );

        promise.always(function() {
          
        });
      }
    });

    return Model;
  })();

  var model = new profile.index.IndexModel(profile.index.data);
  ko.applyBindings(model);
})(ko, profile, new profile.index.ProfileRepository());
