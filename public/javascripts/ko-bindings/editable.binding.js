/* global ko, jQuery */

(function(ko, $) {
  'use strict';

  ko.bindingHandlers.editable = {
    init: function(element, valueAccessor) {
      var observable = valueAccessor(),
        jElement = $(element);
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
            if ($.trim(value) === '') {
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
    }
  };
})(ko, jQuery);