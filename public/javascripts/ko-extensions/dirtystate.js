/* global ko */

(function (ko) {
  'use strict';

  ko.DirtyFlag = function(root, isInitiallyDirty) {
    var result = function() {},
      _initialState = ko.observable(ko.toJSON(root)),
      _isInitiallyDirty = ko.observable(isInitiallyDirty);

    result.isDirty = ko.computed(function() {
      return _isInitiallyDirty() || _initialState() !== ko.toJSON(root);
    });

    result.reset = function() {
      _initialState(ko.toJSON(root));
      _isInitiallyDirty(false);
    };

    return result;
  };
})(ko);
