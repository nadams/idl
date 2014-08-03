/* global ko, jQuery */

(function(ko, $) {
  'use strict';

  var eventHandler = function(e, data, f) {
    if (e.stopPropagation) {
      e.stopPropagation();
    } else {
      e.cancelBubble = true;
    }

    f(data);

    return false;
  };

  ko.bindingHandlers.fileupload = {
    init: function(element, valueAccessor) {
      var options = valueAccessor(),
        fileUpload = $(element).fileupload(options || {});

      if (options.done) {
        fileUpload.on('fileuploaddone', function(e, data) {
          eventHandler(e, data, options.done);
        });
      }

      if (options.fail) {
        fileUpload.on('fileuploadfail', function(e, data) {
          eventHandler(e, data, options.fail);
        });
      }

      if (options.start) {
        fileUpload.on('fileuploadstart', function(e) {
          eventHandler(e, {}, options.start);
        });
      }

      if (options.always) {
        fileUpload.on('fileuploadalways', function(e) {
          eventHandler(e, {}, options.always);
        });
      }
    }
  };
})(ko, jQuery);