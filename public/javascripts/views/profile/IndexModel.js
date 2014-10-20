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
      this.successfullyUpdatedPassword = ko.observable(false);
      
      this.currentPasswordError = ko.observable('');
      this.newPasswordError = ko.observable('');
      this.confirmPasswordError = ko.observable('');
      
      this.globalErrors = ko.observableArray([]);

      this.hasGlobalErrors = ko.computed(function() {
        return this.globalErrors().length > 0;
      }, this);
      
      this.hasCurrentPasswordError = ko.computed(function() {
        return !this.nullOrEmpty(this.currentPasswordError());
      }, this);
      
      this.hasNewPasswordError = ko.computed(function() {
        return !this.nullOrEmpty(this.newPasswordError());
      }, this);

      this.hasConfirmPasswordError = ko.computed(function() {
        return !this.nullOrEmpty(this.confirmPasswordError());
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      updatePassword: function() {
        var promise = repository.updatePassword(
          this.currentPassword(),
          this.newPassword(),
          this.confirmPassword(),
          this
        );

        promise.done(function() {
          this.currentPassword('');
          this.newPassword('');
          this.confirmPassword('');
          this.currentPasswordError('');
          this.newPasswordError('');
          this.confirmPasswordError('');
          this.globalErrors.removeAll();
          this.successfullyUpdatedPassword(true);
        });

        promise.fail(function(response) {
          var data = response.responseJSON;

          this.successfullyUpdatedPassword(false);
          this.currentPasswordError(data.currentPasswordError);
          this.newPasswordError(data.currentPasswordError);
          this.confirmPasswordError(data.confirmPasswordError);
          this.globalErrors.removeAll();
          var i;
          for(i = 0; i < data.globalErrors.length; i++) {
            this.globalErrors.push(data.globalErrors[i]);
          }
        });
      },
      nullOrEmpty: function(string) {
        return typeof string === 'undefined' || string.length === 0;
      }
    });

    return Model;
  })();

  var model = new profile.index.IndexModel(profile.index.data);
  ko.applyBindings(model);
})(ko, profile, new profile.index.ProfileRepository());
