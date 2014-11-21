/* global ko, _, moment, admin */

(function(ko, _, moment, admin) {
  'use strict';

  admin.profile.ProfileModel = (function() {
    var Model = function(data) {
      this.displayName = ko.observable();
      this.email = ko.observable();
      this.passwordExpired = ko.observable();
      this.lastLoginDate = ko.observable();
      this.playerNames = ko.observableArray();
    
      this.initialize(data);

      this.lastLoginDateFormatted = ko.computed(function() {
        return moment.utc(this.lastLoginDate()).fromNow();
      }, this);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.displayName(data.displayName);
        this.email(data.email);
        this.passwordExpired(data.passwordExpired);
        this.lastLoginDate(data.lastLoginDate);

        this.playerNames(_.map(data.playernames, function(item) {
          return new admin.profile.PlayerNameModel(item);
        }));
      }
    });

    return Model;
  })();

  admin.profile.PlayerNameModel = (function() {
    var Model = function(data) {
      this.playerId = ko.observable();
      this.playerName = ko.observable();
      this.isApproved = ko.observable();

      this.initialize(data);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.playerId(data.playerId);
        this.playerName(data.playerName);
        this.isApproved(data.isApproved);
      }
    });

    return Model;
  })();
})(ko, _, moment, admin);

(function(ko, admin) {
  'use strict';

  var model = new admin.profile.ProfileModel(admin.profile.data);
  ko.applyBindings(model);
})(ko, admin);
