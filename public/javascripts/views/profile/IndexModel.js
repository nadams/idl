/* global ko, profile */

(function(ko, profile) {
  'use strict';

  profile.index.IndexModel = (function() {
    var Model = function(data) {
      this.profileId = ko.observable();
      this.profileIsPlayer = ko.observable(false);

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
        // Do ajax stuff
        alert('Making profile a player');
      }
    });

    return Model;
  })();

  var model = new profile.index.IndexModel(profile.index.data);
  ko.applyBindings(model);
})(ko, profile);
