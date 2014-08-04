/* global ko, jQuery */

(function(ko, $) {
  'use strict';

  ko.bindingHandlers.fileupload = {
    init: function(element, valueAccessor) {
      $(element).fileupload(valueAccessor() || {});
    }
  };
})(ko, jQuery);