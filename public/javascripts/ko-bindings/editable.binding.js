/* global ko, jQuery */

(function(ko, $) {
  'use strict';

  ko.bindingHandlers.editable = {
    init: function(element, valueAccessor) {
      var observable = valueAccessor(),
        jElement = $(element);

      var validate = function(value) {
        if (observable.required) {
          var valueValidate = function(item) {
            return $.trim(item) !== '';
          };

          if(typeof observable.validate !== 'undefined') {
            return observable.validate(value);
          } else {
            return valueValidate(value);
          }
        }
      };

      jElement.editable({
        type: jElement.attr('type'),
        mode: 'popup',
        url: function(params) {
          var d = new $.Deferred();
          window.setTimeout(function() {
            observable.value(params.value);
            d.resolve();
          }, 0);
          return d.promise();
        },
        validate: function(value) {
          if (observable.required) {
            if (!validate(value)) {
              return observable.required;
            }
          }
        }
      });
      ko.utils.domNodeDisposal.addDisposeCallback(element, function() {
        $(element).editable('destroy');
      });
    },
    update: function(element, valueAccessor) {
      var value = ko.utils.unwrapObservable(valueAccessor().value);
      $(element).editable('setValue', value);
      if(typeof value.dirty !== 'undefined') {
        value.dirty(true);
      }
    }
  };
})(ko, jQuery);