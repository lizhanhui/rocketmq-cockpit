(function (){
	'use strict';
	angular.module('cockpit', [])
	.value('editableOptions', {
	  theme: 'default',
	  buttons: 'right',
	  blurElem: 'cancel',
	  blurForm: 'ignore',
	  activate: 'focus'
	});
})();

(function (){
	'use strict';

	angular.module('cockpit').factory('editableThemes', function() {var themes = {
    //default
    'default': {
      formTpl:      '<form class="editable-wrap"></form>',
      noformTpl:    '<span class="editable-wrap"></span>',
      controlsTpl:  '<span class="editable-controls"></span>',
      inputTpl:     '',
      errorTpl:     '<div class="editable-error" ng-show="$error" ng-bind="$error"></div>',
      buttonsTpl:   '<span class="editable-buttons"></span>',
      submitTpl:    '<button type="submit">save</button>',
      cancelTpl:    '<button type="button" ng-click="$form.$cancel()">cancel</button>'
    },

    //bs2
    'bs2': {
      formTpl:     '<form class="form-inline editable-wrap" role="form"></form>',
      noformTpl:   '<span class="editable-wrap"></span>',
      controlsTpl: '<div class="editable-controls controls control-group" ng-class="{\'error\': $error}"></div>',
      inputTpl:    '',
      errorTpl:    '<div class="editable-error help-block" ng-show="$error" ng-bind="$error"></div>',
      buttonsTpl:  '<span class="editable-buttons"></span>',
      submitTpl:   '<button type="submit" class="btn btn-primary"><span class="icon-ok icon-white"></span></button>',
      cancelTpl:   '<button type="button" class="btn" ng-click="$form.$cancel()">'+
                      '<span class="icon-remove"></span>'+
                   '</button>'

    },

    //bs3
    'bs3': {
      formTpl:     '<form class="form-inline editable-wrap" role="form"></form>',
      noformTpl:   '<span class="editable-wrap"></span>',
      controlsTpl: '<div class="editable-controls form-group" ng-class="{\'has-error\': $error}"></div>',
      inputTpl:    '',
      errorTpl:    '<div class="editable-error help-block" ng-show="$error" ng-bind="$error"></div>',
      buttonsTpl:  '<span class="editable-buttons"></span>',
      submitTpl:   '<button type="submit" class="btn btn-primary"><span class="glyphicon glyphicon-ok"></span></button>',
      cancelTpl:   '<button type="button" class="btn btn-default" ng-click="$form.$cancel()">'+
                     '<span class="glyphicon glyphicon-remove"></span>'+
                   '</button>',

      //bs3 specific prop to change buttons class: btn-sm, btn-lg
      buttonsClass: '',
      //bs3 specific prop to change standard inputs class: input-sm, input-lg
      inputClass: '',
      postrender: function() {
        //apply `form-control` class to std inputs
        switch(this.directiveName) {
          case 'editableText':
          case 'editableSelect':
          case 'editableTextarea':
          case 'editableEmail':
          case 'editableTel':
          case 'editableNumber':
          case 'editableUrl':
          case 'editableSearch':
          case 'editableDate':
          case 'editableDatetime':
          case 'editableTime':
          case 'editableMonth':
          case 'editableWeek':
            this.inputEl.addClass('form-control');
            if(this.theme.inputClass) {
              // don`t apply `input-sm` and `input-lg` to select multiple
              // should be fixed in bs itself!
              if(this.inputEl.attr('multiple') &&
                (this.theme.inputClass === 'input-sm' || this.theme.inputClass === 'input-lg')) {
                  break;
              }
              this.inputEl.addClass(this.theme.inputClass);
            }
          break;
        }

        //apply buttonsClass (bs3 specific!)
        if(this.buttonsEl && this.theme.buttonsClass) {
          this.buttonsEl.find('button').addClass(this.theme.buttonsClass);
        }
      }
    }
  };

  return themes;
});
})();

(function (){
	'use strict';

	angular.module('cockpit').factory('editableController',
	  ['$q', 'editableUtils',
	  function($q, editableUtils) {

	  //EditableController function
	  EditableController.$inject = ['$scope', '$attrs', '$element', '$parse', 'editableThemes', 'editableOptions', '$rootScope', '$compile', '$q'];
	  function EditableController($scope, $attrs, $element, $parse, editableThemes, editableOptions, $rootScope, $compile, $q) {
	    var valueGetter;

	    //if control is disabled - it does not participate in waiting process
	    var inWaiting;

	    var self = this;

	    self.scope = $scope;
	    self.elem = $element;
	    self.attrs = $attrs;
	    self.inputEl = null;
	    self.editorEl = null;
	    self.single = true;
	    self.error = '';
	    self.theme =  editableThemes[editableOptions.theme] || editableThemes['default'];
	    self.parent = {};

	    //to be overwritten by directive
	    self.inputTpl = '';
	    self.directiveName = '';

	    self.useCopy = false;

	    self.single = null;

	    self.buttons = 'right';

	    self.init = function(single) {
	      self.single = single;

	      self.name = $attrs.eName || $attrs[self.directiveName];

	      if($attrs[self.directiveName]) {
	        valueGetter = $parse($attrs[self.directiveName]);
	      } else {
	        throw 'You should provide value for `'+self.directiveName+'` in editable element!';
	      }

	      if (!self.single) {
	        self.buttons = 'no';
	      } else {
	        self.buttons = self.attrs.buttons || editableOptions.buttons;
	      }

	      if($attrs.eName) {
	        self.scope.$watch('$data', function(newVal){
	          self.scope.$form.$data[$attrs.eName] = newVal;
	        });
	      }

	      if($attrs.onshow) {
	        self.onshow = function() {
	          return self.catchError($parse($attrs.onshow)($scope));
	        };
	      }

	      if($attrs.onhide) {
	        self.onhide = function() {
	          return $parse($attrs.onhide)($scope);
	        };
	      }

	      /**
	       * Called when control is cancelled.
	       *
	       * @var {method|attribute} oncancel
	       * @memberOf editable-element
	       */
	      if($attrs.oncancel) {
	        self.oncancel = function() {
	          return $parse($attrs.oncancel)($scope);
	        };
	      }

	      /**
	       * Called during submit before value is saved to model.
	       * See [demo](#onbeforesave).
	       *
	       * @var {method|attribute} onbeforesave
	       * @memberOf editable-element
	       */
	      if ($attrs.onbeforesave) {
	        self.onbeforesave = function() {
	          return self.catchError($parse($attrs.onbeforesave)($scope));
	        };
	      }

	      /**
	       * Called during submit after value is saved to model.
	       * See [demo](#onaftersave).
	       *
	       * @var {method|attribute} onaftersave
	       * @memberOf editable-element
	       */
	      if ($attrs.onaftersave) {
	        self.onaftersave = function() {
	          return self.catchError($parse($attrs.onaftersave)($scope));
	        };
	      }

	      // watch change of model to update editable element
	      // now only add/remove `editable-empty` class.
	      // Initially this method called with newVal = undefined, oldVal = undefined
	      // so no need initially call handleEmpty() explicitly
	      $scope.$parent.$watch($attrs[self.directiveName], function(newVal, oldVal) {
	        self.handleEmpty();
	      });
	    };

	    self.render = function() {
	      var theme = self.theme;

	      //build input
	      self.inputEl = angular.element(self.inputTpl);

	      //build controls
	      self.controlsEl = angular.element(theme.controlsTpl);
	      self.controlsEl.append(self.inputEl);

	      //build buttons
	      if(self.buttons !== 'no') {
	        self.buttonsEl = angular.element(theme.buttonsTpl);
	        self.submitEl = angular.element(theme.submitTpl);
	        self.cancelEl = angular.element(theme.cancelTpl);
	        self.buttonsEl.append(self.submitEl).append(self.cancelEl);
	        self.controlsEl.append(self.buttonsEl);

	        self.inputEl.addClass('editable-has-buttons');
	      }

	      //build error
	      self.errorEl = angular.element(theme.errorTpl);
	      self.controlsEl.append(self.errorEl);

	      //build editor
	      self.editorEl = angular.element(self.single ? theme.formTpl : theme.noformTpl);
	      self.editorEl.append(self.controlsEl);

	      // transfer `e-*|data-e-*|x-e-*` attributes
	      for(var k in $attrs.$attr) {
	        if(k.length <= 1) {
	          continue;
	        }
	        var transferAttr = false;
	        var nextLetter = k.substring(1, 2);

	        // if starts with `e` + uppercase letter
	        if(k.substring(0, 1) === 'e' && nextLetter === nextLetter.toUpperCase()) {
	          transferAttr = k.substring(1); // cut `e`
	        } else {
	          continue;
	        }

	        // exclude `form` and `ng-submit`,
	        if(transferAttr === 'Form' || transferAttr === 'NgSubmit') {
	          continue;
	        }

	        // convert back to lowercase style
	        transferAttr = transferAttr.substring(0, 1).toLowerCase() + editableUtils.camelToDash(transferAttr.substring(1));

	        // workaround for attributes without value (e.g. `multiple = "multiple"`)
	        var attrValue = ($attrs[k] === '') ? transferAttr : $attrs[k];

	        // set attributes to input
	        self.inputEl.attr(transferAttr, attrValue);
	      }

	      self.inputEl.addClass('editable-input');
	      self.inputEl.attr('ng-model', '$data');

	      // add directiveName class to editor, e.g. `editable-text`
	      self.editorEl.addClass(editableUtils.camelToDash(self.directiveName));

	      if(self.single) {
	        self.editorEl.attr('editable-form', '$form');
	        // transfer `blur` to form
	        self.editorEl.attr('blur', self.attrs.blur || (self.buttons === 'no' ? 'cancel' : editableOptions.blurElem));
	      }

	      //apply `postrender` method of theme
	      if(angular.isFunction(theme.postrender)) {
	        theme.postrender.call(self);
	      }

	    };

	    // with majority of controls copy is not needed, but..
	    // copy MUST NOT be used for `select-multiple` with objects as items
	    // copy MUST be used for `checklist`
	    self.setLocalValue = function() {
	      self.scope.$data = self.useCopy ?
	        angular.copy(valueGetter($scope.$parent)) :
	        valueGetter($scope.$parent);
	    };

	    //show
	    self.show = function() {
	      // set value of scope.$data
	      self.setLocalValue();

	      /*
	      Originally render() was inside init() method, but some directives polluting editorEl,
	      so it is broken on second openning.
	      Cloning is not a solution as jqLite can not clone with event handler's.
	      */
	      self.render();

	      // insert into DOM
	      $element.after(self.editorEl);

	      // compile (needed to attach ng-* events from markup)
	      $compile(self.editorEl)($scope);

	      // attach listeners (`escape`, autosubmit, etc)
	      self.addListeners();

	      // hide element
	      $element.addClass('editable-hide');

	      // onshow
	      return self.onshow();
	    };

	    //hide
	    self.hide = function() {
	      self.editorEl.remove();
	      $element.removeClass('editable-hide');

	      // onhide
	      return self.onhide();
	    };

	    // cancel
	    self.cancel = function() {
	      // oncancel
	      self.oncancel();
	      // don't call hide() here as it called in form's code
	    };

	    /*
	    Called after show to attach listeners
	    */
	    self.addListeners = function() {
	      // bind keyup for `escape`
	      self.inputEl.bind('keyup', function(e) {
	          if(!self.single) {
	            return;
	          }

	          // todo: move this to editable-form!
	          switch(e.keyCode) {
	            // hide on `escape` press
	            case 27:
	              self.scope.$apply(function() {
	                self.scope.$form.$cancel();
	              });
	            break;
	          }
	      });

	      // autosubmit when `no buttons`
	      if (self.single && self.buttons === 'no') {
	        self.autosubmit();
	      }

	      // click - mark element as clicked to exclude in document click handler
	      self.editorEl.bind('click', function(e) {
	        // ignore right/middle button click
	        if (e.which !== 1) {
	          return;
	        }

	        if (self.scope.$form.$visible) {
	          self.scope.$form._clicked = true;
	        }
	      });
	    };

	    // setWaiting
	    self.setWaiting = function(value) {
	      if (value) {
	        // participate in waiting only if not disabled
	        inWaiting = !self.inputEl.attr('disabled') &&
	                    !self.inputEl.attr('ng-disabled') &&
	                    !self.inputEl.attr('ng-enabled');
	        if (inWaiting) {
	          self.inputEl.attr('disabled', 'disabled');
	          if(self.buttonsEl) {
	            self.buttonsEl.find('button').attr('disabled', 'disabled');
	          }
	        }
	      } else {
	        if (inWaiting) {
	          self.inputEl.removeAttr('disabled');
	          if (self.buttonsEl) {
	            self.buttonsEl.find('button').removeAttr('disabled');
	          }
	        }
	      }
	    };

	    self.activate = function() {
	      setTimeout(function() {
	        var el = self.inputEl[0];
	        if (editableOptions.activate === 'focus' && el.focus) {
	          el.focus();
	        }
	        if (editableOptions.activate === 'select' && el.select) {
	          el.select();
	        }
	      }, 0);
	    };

	    self.setError = function(msg) {
	      if(!angular.isObject(msg)) {
	        $scope.$error = msg;
	        self.error = msg;
	      }
	    };

	    /*
	    Checks that result is string or promise returned string and shows it as error message
	    Applied to onshow, onbeforesave, onaftersave
	    */
	    self.catchError = function(result, noPromise) {
	      if (angular.isObject(result) && noPromise !== true) {
	        $q.when(result).then(
	          //success and fail handlers are equal
	          angular.bind(this, function(r) {
	            this.catchError(r, true);
	          }),
	          angular.bind(this, function(r) {
	            this.catchError(r, true);
	          })
	        );
	      //check $http error
	      } else if (noPromise && angular.isObject(result) && result.status &&
	        (result.status !== 200) && result.data && angular.isString(result.data)) {
	        this.setError(result.data);
	        //set result to string: to let form know that there was error
	        result = result.data;
	      } else if (angular.isString(result)) {
	        this.setError(result);
	      }
	      return result;
	    };

	    self.save = function() {
	      valueGetter.assign($scope.$parent, angular.copy(self.scope.$data));

	      // no need to call handleEmpty here as we are watching change of model value
	      // self.handleEmpty();
	    };

	    /*
	    attach/detach `editable-empty` class to element
	    */
	    self.handleEmpty = function() {
	      var val = valueGetter($scope.$parent);
	      var isEmpty = val === null || val === undefined || val === "" || (angular.isArray(val) && val.length === 0);
	      $element.toggleClass('editable-empty', isEmpty);
	    };

	    /*
	    Called when `buttons = "no"` to submit automatically
	    */
	    self.autosubmit = angular.noop;

	    self.onshow = angular.noop;
	    self.onhide = angular.noop;
	    self.oncancel = angular.noop;
	    self.onbeforesave = angular.noop;
	    self.onaftersave = angular.noop;
	  }

	  return EditableController;
	}]);
})();
