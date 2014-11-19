/* global ko, _, admin */

(function(ko, _, admin, repository) {
  'use strict';

  admin.profile = admin.profile || {};
  admin.profile.IndexModel = (function() {
    var Model = function(data) {
      this.profileSearchValue = ko.observable().extend({ throttle: 250 });
      this.results = ko.observableArray([]);

      this.profileSearchValue.subscribe(function(newValue) {
        var value = this.profileSearchValue();

        if(value.length) {
          var promise = repository.searchProfiles(value, this);
          promise.done(function(data) {
            this.results.removeAll();
            this.results(_.map(data, function(item) { 
              return new admin.profile.SearchResultModel(item);
            }));
          });

          promise.fail(function(data) {
          
          });

          promise.always(function(data) {
          
          });
        }
      }, this);
    };

    return Model;
  })();

  admin.profile.SearchResultModel = (function() {
    var Model = function(data) {
      this.profileId = ko.observable();
      this.displayName = ko.observable();
      this.email = ko.observable();
      this.playerNames = ko.observableArray();

      this.initialize(data);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
        this.profileId(data.profileId);
        this.displayName(data.displayName);
        this.email(data.email);
        this.playerNames(_.map(data.playerNames, function(item) {
          return new admin.profile.SearchResultPlayerNameModel(item);
        }));
      }
    });

    return Model;
  })();

  admin.profile.SearchResultPlayerNameModel = (function() {
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
})(ko, _, admin, new admin.profile.ProfileRepository());

(function(ko, admin) {
  'use strict';

  var model = new admin.profile.IndexModel({});
  ko.applyBindings(model);
})(ko, admin);
