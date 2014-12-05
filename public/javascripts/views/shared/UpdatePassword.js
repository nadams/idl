/* global ko, idl */

(function(ko, idl) {
  idl.shared = idl.shared || {};
  idl.shared.UpdatePasswordModel = (function() {
    var Model = function(repository) {
      this.repository = repository;
      this.currentPassword = ko.observable('');
      this.newPassword = ko.observable('');
      this.confirmPassword = ko.observable('');

      this.successfullyUpdatedPassword = ko.observable(false);

      this.currentPasswordError = ko.observable('');
      this.newPasswordError = ko.observable('');
      this.confirmPasswordError = ko.observable('');
      this.globalErrors = ko.observableArray([]);

      this.hasCurrentPasswordError = ko.computed(function() {
        return !idl.nullOrEmpty(this.currentPasswordError());
      }, this);

      this.hasNewPasswordError = ko.computed(function() {
        return !idl.nullOrEmpty(this.newPasswordError());
      }, this);

      this.hasConfirmPasswordError = ko.computed(function() {
        return !idl.nullOrEmpty(this.confirmPasswordError());
      }, this);

      this.hasGlobalErrors = ko.computed(function() {
        return this.globalErrors().length > 0;
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      updatePassword: function() {
        var promise = this.repository.updatePassword(
          this.currentPassword(),
          this.newPassword(),
          this.confirmPassword(),
          this
        );

        promise.done(function() {
          this.clearPasswordForm();
          this.successfullyUpdatedPassword(true);
          this.globalErrors.removeAll();
        });

        promise.fail(function(response) {
          var data = response.responseJSON;

          this.successfullyUpdatedPassword(false);
          this.currentPasswordError(data.currentPasswordError);
          this.newPasswordError(data.newPasswordError);
          this.confirmPasswordError(data.confirmPasswordError);
          this.globalErrors.removeAll();
          this.globalErrors(data.globalErrors);
        });
      },
      clearPasswordForm: function() {
        this.currentPassword('');
        this.newPassword('');
        this.confirmPassword('');
        this.currentPasswordError('');
        this.newPasswordError('');
        this.confirmPasswordError('');
        this.globalErrors.removeAll();
      }
    });

    return Model;
  })();
})(ko, idl);
