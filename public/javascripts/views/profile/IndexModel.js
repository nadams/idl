/* global ko */

(function(ko) {
  'use strict';

  var profile = profile || {};
  profile.index = profile.index || {};
  profile.index.IndexModel = (function() {
    var Model = function(data) {
    
      this.initialize(data);
    };

    ko.utils.extend(Model.prototype, {
      initialize: function(data) {
      
      }
    });

    return Model;
  })();
})(ko);
