/*jslint browser: true, evil: false, plusplus: true */
/*global $ */

(function (globals) {
    'use strict';

    var DVSA = {

        Modules: {},

        Utilities: {},

        // Events: $({}), // Example of using JQuery for events.
        Events: false,

        init: function (events) {
            var x;

            this.Events = events || false;

            for (x in DVSA.Modules) {
                if (DVSA.Modules.hasOwnProperty(x) && typeof (DVSA.Modules[x].init) === "function") {
                    DVSA.Modules[x].init();
                }
            }
        }
    };

    globals.DVSA = DVSA;
}(this));

/**
 * This file is part of the DVSA MOT Frontend project.
 *
 * @link http://gitlab.clb.npm/mot/mot
 */

(function( dvsaMotFrontendAuthentication, $, undefined ) {
    /**
     * @param timeOut in seconds. Defaults to 60 seconds.
     *
     */
    dvsaMotFrontendAuthentication.keepSessionAlive = function(timeOut) {

        var timeOut = typeof timeOut !== 'undefined' ? parseInt(timeOut) : 60;
        if (timeOut <= 0) {
            return;
        }

        setInterval(function() {
            $.get(window.location.href);
        }, timeOut * 1000);
    };

    /**
     * Add non-autocomplete usernameFields.
     *
     * - Override the form submit event
     * - Added dynamic elements
     * - On submit copy dynamic usernameField values to real usernameFields
     */
    dvsaMotFrontendAuthentication.addNonAutocompleteFields = function() {

        var usernameField = $('#IDToken1').eq(0);
        var passwordField = $('#IDToken2').eq(0);
        var form          = $('#Login').eq(0);
        var uniqueId      = new Date().getTime();

        var fakeUsernameField = usernameField.clone().prop({
            id: uniqueId + '_tid1',
            name: uniqueId + '_tid1',
            tabindex: 1
        }).insertAfter(usernameField);

        var fakePasswordField = passwordField.clone().prop({
            id: uniqueId + '_tid2',
            name: uniqueId + '_tid2',
            tabindex: 2
        }).insertAfter(passwordField);

        $.each([usernameField, passwordField], function(k, field) {
            setTimeout(function() {
                field.val('');
            }, 250);
            field.css({
                'width': '1px',
                'position': 'absolute',
                'left': '-9999px'
            });
            field.prop('tabindex', -1);
        });

        form.prop('id', uniqueId + '_tid_form');
        form.prop('autocomplete', 'off');

        fakeUsernameField.focus();

        form.submit(function( event ) {
            usernameField.val(fakeUsernameField.val());
            passwordField.val(fakePasswordField.val());

            return true;
        });
    };

}( window.dvsaMotFrontendAuthentication = window.dvsaMotFrontendAuthentication || {}, jQuery ));

$( document ).ready(function() {
    dvsaMotFrontendAuthentication.addNonAutocompleteFields();
});

/*

DVSA Password module

*/

(function(globals) {
    'use strict';
    var init,
        Criteria,
        DVSA = globals.DVSA || {};

    Criteria = function Criteria(options) {

        var options = options || {};

        this.trigger = options.trigger || null;
        this.scope = options.criteria || null;

        this.stateNeutral = 'criteria__criterion';
        this.stateFail = 'criteria__criterion--has-failed';
        this.statePass = 'criteria__criterion--has-passed';

        this.criteria = [];

        this.getCriteria();
        this.bindEvents();

    };

    Criteria.prototype.getCriteria = function() {

        var criteriaElements = globals.document.querySelectorAll('[data-criteria]');

        for (var i = 0; i < criteriaElements.length; i++) {
            var criteriaName = criteriaElements[i].getAttribute('data-criteria');
            var criteriaParam = criteriaElements[i].getAttribute('data-criteria-param') || null;
            this.criteria.push({
                name: criteriaName,
                param: criteriaParam,
                state: criteriaElements[i].className,
                element: criteriaElements[i]
            });
        }
    };

    Criteria.prototype.bindEvents = function() {
        var self = this;
        if (globals.window.addEventListener) {
            this.trigger.addEventListener("keyup", function() {
                self.checkCriteria();
            });
            this.trigger.addEventListener("paste", function() {
                self.checkCriteria();
            });
        } else if (globals.window.attachEvent) {
            this.trigger.attachEvent("onkeyup", function() {
                self.checkCriteria();
            });
            this.trigger.attachEvent("onpaste", function() {
                self.checkCriteria();
            });
        }
    };

    Criteria.prototype.checkCriteria = function() {

        var currentValue = this.trigger.value;

        for (var i = 0; i < this.criteria.length; i++) {
            var criteriaName = this.criteria[i].name;
            var criteriaParam = this.criteria[i].param;

            var status = this[criteriaName](currentValue, criteriaParam);

            this.decorateElement(i, status);

        }
    };

    Criteria.prototype.decorateElement = function(i, state) {
        if (this.criteria[i].state != state) {
            this.criteria[i].state = state;
            this.criteria[i].element.className = state;
        }
    };

    Criteria.prototype.hasMixedCase = function(val) {
        var cssClass = this.stateNeutral;

        if ((/^(?=.*[a-z])(?=.*[A-Z]).+$/.test(val))) {
            cssClass = this.statePass;
        }

        return cssClass;
    };

    Criteria.prototype.minLength = function(val, param) {
        var cssClass = this.stateNeutral;
        var regEx = new RegExp("^.{" + param + ",}$");

        if ((regEx.test(val))) {
            cssClass = this.statePass;
        }

        return cssClass;
    };

    Criteria.prototype.notMatch = function(val, param) {
        var cssClass = this.stateNeutral;
        var val_lc = val.toLowerCase();
        var param_lc = param.toLowerCase();


        if (val_lc === param_lc && val_lc.length === param_lc.length) {
            cssClass = this.stateFail;
        } else if (val_lc != param_lc.substring(0, val_lc.length)) {
            cssClass = this.statePass;
        }

        return cssClass;
    };

    Criteria.prototype.hasNumeric = function(val) {
        var cssClass = this.stateNeutral;

        if ((/[0-9]/.test(val))) {
            cssClass = this.statePass;
        }

        return cssClass;
    };

    Criteria.prototype.hasUpperCase = function(val) {
        var cssClass = this.stateNeutral;

        if ((/[A-Z]/.test(val))) {
            cssClass = this.statePass;
        }

        return cssClass;
    };

    Criteria.prototype.hasLowerCase = function(val) {
        var cssClass = this.stateNeutral;

        if ((/[a-z]/.test(val))) {
            cssClass = this.statePass;
        }

        return cssClass;
    };

    init = function () {
    };

    // public
    globals.DVSACriteria = {
        init: init,
        Criteria: Criteria 
    };
}(this));


/*
    simpler show/hide toggle
    example:
    <a href="#" data-action="toggle" data-open-text="Hide RFR details" data-target="rfrList" id="toggleRFRList" class="js-only">Show added RFR details</a>
    data-action="toggle" // define action applied to element
    data-open-text="Hide RFR details" // text to swap out once clicked
    data-target="rfrList" // ID of target
*/

(function (globals) {
    'use strict';
    var init,
        showerHider,
        ShowHideToggle,
        DVSA = globals.DVSA || {};

    ShowHideToggle = function ShowHideToggle() {};

    function isHidden(el) {
        // This does not work in older version of IE annoyingly
        // return (el.offsetParent === null); 

        // But this hideous monstrosity does
        // TODO: Ditch this in favour of above or stop supporting <=IE9
        var style = window.getComputedStyle(el);
        return (style.display === 'none');
    }

    ShowHideToggle.prototype.init = function(){

        var self = this;
        this.triggerElements = document.querySelectorAll('[data-action="showHideToggle"]');

        for (var i=0; i < this.triggerElements.length; i++){
            (function () {
                var triggerElement = self.triggerElements[i],
                    triggerElementStartText = triggerElement.text,
                    triggerElementOpenText = triggerElement.getAttribute('data-open-text'),
                    targetId = triggerElement.getAttribute('data-target'),
                    toggleType = triggerElement.getAttribute('data-toggle-type'),
                    targetElement = document.querySelector('#' + targetId),
                    footerTriggerElement = self.triggerElements[1];
                    // use the less particular jQuery to be a bit more flexible about what elements we can target, either one # or many .
                    // targetElement = $(targetId);

                triggerElement.setAttribute('data-closed-text', triggerElementStartText);

                // add class to target element in case it doesn't have one
                // a lapse into jQuery to save adding tons of extra code to check class list etc
                // seeing as we're loading it anyway...
                if(toggleType != 'responsive') {
                    $(targetElement).addClass(' js-hidden');
                }

                if (window.addEventListener) {
                    triggerElement.addEventListener('click', function(e) {
                        e.preventDefault();
                        self.checkState(triggerElement, targetElement, triggerElementStartText, triggerElementOpenText, targetId);
                    });

                    footerTriggerElement.addEventListener('click', function(e) {
                        e.preventDefault();
                        self.scrollToTable(targetId);
                    });
                }
            }());
        }
    };

    ShowHideToggle.prototype.checkState = function(triggerElement, targetElement, triggerElementStartText, triggerElementOpenText, toggleType){

        if(isHidden(targetElement)) {
            
            this.showToggle(targetElement, triggerElementOpenText, toggleType);
        } else {
            
            this.hideToggle(targetElement, triggerElementStartText, toggleType);
        }
    };

    // OK, lapsing into more jQuery as time is of the essence...
    ShowHideToggle.prototype.showToggle = function(targetElement, triggerElementOpenText, toggleType) {

        if(toggleType == 'responsive') {

            $(targetElement).removeClass('hide-small');

        } else {

            $(targetElement).removeClass('js-hidden');

        }

        $('[data-action="showHideToggle"]').each(function(){
            $(this).text(triggerElementOpenText);
            $(this).removeClass('toggle-switch').addClass('toggle-switch--open');
        });
    };

    ShowHideToggle.prototype.hideToggle = function(targetElement, triggerElementStartText, toggleType) {

        if(toggleType == 'responsive') {

            $(targetElement).addClass('hide-small');

        } else {

             $(targetElement).addClass('js-hidden');
        }

        $('[data-action="showHideToggle"]').each(function(){
            $(this).text(triggerElementStartText);
            $(this).removeClass('toggle-switch--open').addClass('toggle-switch');
        });
    };

    ShowHideToggle.prototype.scrollToTable = function(targetId) {
        // If the bottom link is changed, then pull the "header bar" of the active area back into focus at the top of the screen to prevent user from getting lost
        $('#' + targetId + 'Parent')[0].scrollIntoView(true);
    };

    // use below to init on all pages
    // showerHider = new ShowHideToggle();
    // this has been left in, in case requirements change
    init = function () {
        //showerHider.init();
    };

    globals.DVSAShowHideToggle = {
        init: init,
        showerHider: ShowHideToggle
    };

}(this));

/*
    simpler show/hide toggle to deal with multiple toggled items labelled with a class
    example:

    <div class="js-toggle-this-stuff js-hidden">
        Some stuff...
    </div>

    If JS is on, this is how the anchor part appears in the DOM:

    <a href="#" data-action="showHideToggle" data-open-text="Hide stuff" data-toggle-class="js-toggle-this-stuff" class="toggle-switch layer-table__link js-only" data-closed-text="Show stuff">Show stuff</a>

    Note: the data-closed-text attr is added automatically from the existing text
*/

(function (globals) {
    'use strict';
    var init,
        showerHiderClasses,
        ShowHideToggleClasses,
        DVSA = globals.DVSA || {};

    // Create empty function upon which to hang the prototypes below
    ShowHideToggleClasses = function ShowHideToggleClasses() {};

    function isHidden(targetClassName) {
        var firstElementToCheck = $('.' + targetClassName).first();
        return (!$(firstElementToCheck).is(':visible'));
    }

    ShowHideToggleClasses.prototype.init = function(){

        var self = this;
        this.triggerElements = document.querySelectorAll('[data-action="showHideToggle"]');

        for (var i=0; i < this.triggerElements.length; i++){
            (function () {
                var returnableID = 'inpageLink_' + new Date().getTime();

                var triggerElement = self.triggerElements[i],
                    triggerElementStartText = triggerElement.text,
                    triggerElementOpenText = triggerElement.getAttribute('data-open-text'),
                    targetClassName = triggerElement.getAttribute('data-toggle-class'),
                    toggleType = triggerElement.getAttribute('data-toggle-type'),
                    footerTriggerElement = self.triggerElements[1];

                triggerElement.setAttribute('data-closed-text', triggerElementStartText);

                // add class to target element in case it doesn't have one
                if(toggleType != 'responsive') {
                    $('.' + targetClassName).addClass(' js-hidden');
                }

                if (window.addEventListener) {
                    triggerElement.addEventListener('click', function(e) {
                        e.preventDefault();
                        self.checkState(targetClassName, triggerElementStartText, triggerElementOpenText, toggleType, footerTriggerElement, returnableID);
                    });

                    if(footerTriggerElement) {
                        $(triggerElement).parents('.defect-summary').attr('id', returnableID);
                    }
                }
            }());
        }
    };

    ShowHideToggleClasses.prototype.checkState = function(targetClassName, triggerElementStartText, triggerElementOpenText, toggleType, footerTriggerElement, returnableID){

        if(isHidden(targetClassName)) {

            this.showToggle(targetClassName, triggerElementOpenText, toggleType);

        } else {

            this.hideToggle(targetClassName, triggerElementStartText, toggleType, footerTriggerElement, returnableID);
        }
    };

    ShowHideToggleClasses.prototype.showToggle = function(targetClassName, triggerElementOpenText, toggleType) {

        if(toggleType == 'responsive') {

            $('.' + targetClassName).removeClass('hide-small');

        } else {

            $('.' + targetClassName).removeClass('js-hidden');
        }

        $('[data-toggle-class="' + targetClassName + '"]').each(function(){
            $(this).text(triggerElementOpenText);
            $(this).removeClass('toggle-switch').addClass('toggle-switch--open');
        });
    };

    ShowHideToggleClasses.prototype.hideToggle = function(targetClassName, triggerElementStartText, toggleType, footerTriggerElement, returnableID) {

        if(toggleType == 'responsive') {

            $('.' + targetClassName).addClass('hide-small');

        } else {

            $('.' + targetClassName).addClass('js-hidden');
        }


        if(footerTriggerElement) {
            // TODO: needs refactoring
            // $('#' + returnableID)[0].scrollIntoView(true);
        }

        $('[data-toggle-class="' + targetClassName + '"]').each(function(){
            $(this).text(triggerElementStartText);
            $(this).removeClass('toggle-switch--open').addClass('toggle-switch');
        });
    };

    // use below to init on all pages
    // showerHider = new ShowHideToggle();
    // this has been left in, in case requirements change
    init = function () {
        // showerHider.init();
    };

    globals.DVSAShowHideToggleClasses = {
        init: init,
        showerHiderClasses: ShowHideToggleClasses
    };

}(this));

/*
    mark repairs in basket
*/
(function (globals) {
    'use strict';

    var init,
        markRepairs,
        MarkRepairs,
        DVSA = globals.DVSA || {};

    MarkRepairs = function MarkRepairs() {};

    MarkRepairs.prototype.init = function(){

        function updateBrakeTest (brakeTestOutcome, brakesTested, brakeResults, disableSubmitButton) {
            if (!!$('.js-brakeTestActionPanel').length ) {
                var $brakeTestStatus = $('.js-brakeTestStatus'),
                    $addBrakeTest = $('.js-addBrakeTest'),
                    $brakeTestActions = $('.js-brakeTestActions'),
                    $reviewTestButton = $('.js-reviewTestButton');

                $brakeTestStatus.text(brakeTestOutcome);

                if (!!$brakeTestActions.length) {
                    if (true === brakesTested && true === brakeResults) {
                        $brakeTestActions.removeClass('u-hidden');
                    } else {
                        $brakeTestActions.addClass('u-hidden');
                    }
                }

                if (!!$brakeTestActions.length) {
                    if(false === brakesTested || true === brakeResults) {
                        $addBrakeTest.addClass('u-hidden');
                    } else {
                        $addBrakeTest.removeClass('u-hidden');
                    }
                }

                if (!!$reviewTestButton.length) {
                    if (true === disableSubmitButton) {
                        $reviewTestButton.attr('disabled', true);
                    } else {
                        $reviewTestButton.attr('disabled', false);
                    }
                }

            }
        }

        function updateCount (type, action) {
            var numberOfFailures = $('.js-numberOfFailures'),
                valNumberOfFailures = parseInt(numberOfFailures.first().text()),
                numberOfAdvisories = $('.js-numberOfAdvisories'),
                valNumberOfAdvisories = parseInt(numberOfAdvisories.first().text());

            if (action == 'repair') {
                if (type == 'advisory' && !!numberOfAdvisories.length) {
                    numberOfAdvisories.text(valNumberOfAdvisories - 1);
                }else if (type == 'failure' && !!numberOfFailures.length){
                    numberOfFailures.text(valNumberOfFailures - 1);
                }
            }else{
                if (type == 'advisory' && !!numberOfAdvisories.length) {
                    numberOfAdvisories.text(valNumberOfAdvisories + 1);
                }else if (type == 'failure' && !!numberOfFailures.length){
                    numberOfFailures.text(valNumberOfFailures + 1);
                }
            }
        }

        $(document).on('click', '.js-buttonMarkRepaired', function(e){
            e.preventDefault();

            var self = $(this),
                rfrForm = self.parents('.js-rfrForm'),
                url = rfrForm.attr('action') || self.data('url'),
                formData = rfrForm.serialize() || self.data('form'),
                rfrItem = self.parents('.js-rfrItem'),
                itemStatus = rfrItem.find('.js-itemStatus'),
                isLoading;

            self.attr('disabled', true);

            $.ajax({
                type:"POST",
                url: url,
                data: formData,
                beforeSend: function () {
                    isLoading = setTimeout(function(){
                        rfrItem.addClass('has-status');
                        itemStatus.html('Loading');
                    }, 2000);
                },
                success: function (data) {
                    clearTimeout(isLoading);

                    if (data.success) {

                        rfrItem.removeClass('has-status');

                        if (data.action == 'repair') {

                            rfrItem.addClass('has-success');
                            updateCount(data.defectType, data.action);

                        } else {
                            rfrItem.removeClass('has-success');
                            updateCount(data.defectType, data.action);
                        }

                        updateBrakeTest(data.brakeTestOutcome, data.brakesTested, data.brakeTestResults, data.disableSubmitButton);

                    } else {
                        rfrItem.addClass('has-status');
                        itemStatus.html('That didn\'t work, <a class="js-buttonMarkRepaired" href="" data-url="'+url+'" data-form="'+formData+'">try again</a>');
                    }
                },
                error: function() {
                    clearTimeout(isLoading);
                    rfrItem.addClass('has-status');
                    itemStatus.html('That didn\'t work, <a class="js-buttonMarkRepaired" href="" data-url="'+url+'" data-form="'+formData+'">try again</a>');
                },
                complete: function() {
                    self.attr('disabled', false);
                }
            });

        });
    };

    globals.DVSAMarkRepairs = {
        markRepairs: MarkRepairs
    };

}(this));

/*jslint browser: true, evil: false, plusplus: true */
/*global DVSA, $, console */

/*

DVSA cookie feature test module

*/

(function(globals) {
    'use strict';
    var init,
        CookiesEnabled,
        DVSA = globals.DVSA || {};

    CookiesEnabled = function CookiesEnabled() {

        var cookieName = "cookeTest";
        try {
            // Create cookie
            document.cookie = cookieName + '=1';
            var ret = document.cookie.indexOf(cookieName + '=') != -1;
            // Delete cookie
            document.cookie = cookieName + '=1; expires=Thu, 01-Jan-1970 00:00:01 GMT';
            return ret;
        } catch (e) {
            return false;
        }

    };

    // public
    globals.DVSACookiesEnabled = {
        CookiesEnabled: CookiesEnabled 
    };
}(this));

/*
    multiple form submit prevention
    example:
    <form name="form-name" class="prevent-double-click-form" data-submit-button-id="confirm_test_result">
        <input type="submit" class="button" value="Some value" id="confirm_test_result">
    </formn>
    <script type="text/javascript">
        DoubleClickPrevention.start();
    </script>

    name="form-name" // you have to add some name to your form
    class="prevent-double-click-form" // add this class to enable multiple form submit prevention
    data-submit-button-id="confirm_test_result" // id of a submit button element that will be disabled after form submit
*/
(function (globals) {
    'use strict';
    var start,
        doubleClickPrevention,
        DoubleClickPrevention,
        DVSA = globals.DVSA || {};

    DoubleClickPrevention = function DoubleClickPrevention() {};

    DoubleClickPrevention.prototype.start = function(){
        var validatedForms = {};

        $('.prevent-double-click-form').submit(function (e) {
            if(!validatedForms.hasOwnProperty(this.name)){
                validatedForms[this.name] = true;
                var submitId = $(this).data('submit-button-id');
                var submitButton = $('#' + submitId);
                submitButton.attr('disabled', 'disabled');

                return true;
            } else {
                e.preventDefault();
                return false;
            }
        });
    };

    doubleClickPrevention = new DoubleClickPrevention();

    start = function () {
        doubleClickPrevention.start();
    };

    globals.DoubleClickPrevention = {
        start: start
    };
}(this));

/*jslint browser: true, evil: false, plusplus: true */
/*global DVSA, $, console */

/*
    TODO:

        1. Mustard test
        2. Refactor closure
        3. Investigate/implement .bind
        4. Keep element state in scope to reduce repaint (nice to have)
        5. Test the target element exists: document.querySelector('#' + targetId); 
        6. Write unit tests
        7. Construct 'targetVals' array once (not on every change)
        8. Refactor clearData to The Observer Pattern (https://carldanley.com/js-observer-pattern/)
*/

(function (globals) {
    'use strict';
    var init,
        selectToggle,
        SelectToggle,
        DVSA = globals.DVSA || {};

    SelectToggle = function SelectToggle() {};

    SelectToggle.prototype.init = function(){
        var self = this;
        
        this.triggerElements = document.querySelectorAll("select[data-target]");

        for (var i=0; i < this.triggerElements.length; i++){
            (function () {
                var triggerEl = self.triggerElements[i];
                var targetId = triggerEl.getAttribute('data-target');
                var targetEl = document.querySelector('#' + targetId);  // Needs test
                var targetVal = triggerEl.getAttribute('data-target-value');

                // ARIA attributes
                triggerEl.setAttribute('aria-controls', targetId);

                if (window.addEventListener) {
                    triggerEl.addEventListener("keyup", function() {
                        self.checkState(triggerEl, targetEl, targetVal);
                    });
                    triggerEl.addEventListener("change", function() {
                        self.checkState(triggerEl, targetEl, targetVal);
                    });
                }

                self.checkState(triggerEl, targetEl, targetVal);
            }());
        }
    };

    SelectToggle.prototype.checkState = function(triggerEl, targetEl, targetVal){
        
        var triggerMet = false;
        var targetVals = this.getTargetValues(targetVal);
        
        for (var i=0; i < targetVals.length; i++){
            if (triggerEl.value === targetVals[i]) {
                triggerMet = true;
            }
        }

        if (triggerMet === true) {
            this.showContent(triggerEl, targetEl);
        }else{
            this.hideContent(triggerEl, targetEl);
            this.clearData(targetEl);
        }
    };

    SelectToggle.prototype.getTargetValues = function(targetVal){
        return targetVal.split(',');
    };

    SelectToggle.prototype.clearData = function(target){
        var i;
        var inputList = target.querySelectorAll(
                'textarea:not([value=""]), ' +
                'input[type="text"], ' +
                'input[type="email"], ' +
                'input[type="password"], ' +
                'input[type="tel"]'
            );

        for (i = 0; i < inputList.length; ++i) {
            var leaveContentSwitch = $(inputList[i]).data('retain-value');
            if(leaveContentSwitch !== true) {
                inputList[i].value = '';
            }
        }
    };

    SelectToggle.prototype.hideContent = function(trigger, target){
        target.style.display = 'none';
        // ARIA attributes
        target.setAttribute('aria-hidden', 'true');
        trigger.setAttribute('aria-expanded', 'false');
    };

    SelectToggle.prototype.showContent = function(trigger, target){
        target.style.display = 'block';
        // ARIA attributes
        target.setAttribute('aria-hidden', 'false');
        trigger.setAttribute('aria-expanded', 'true');
    };

    selectToggle = new SelectToggle();

    init = function () {
        selectToggle.init();
    };

    globals.DVSASelectToggle = {
        init: init,
        selectToggle: selectToggle
    };
}(this));

/*jslint browser: true, evil: false, plusplus: true */
/*global DVSA, $, console */

(function (globals) {
    'use strict';
    var init,
        loadingMessage,
        DVSA = globals.DVSA || {};

    init = function () {
        loadingMessage();
    };

    loadingMessage = function () {
        globals.console.log('DVSA module template loaded.');
    };

    // public
    globals.DVSAModuleTemplate = {
        init: init
    };
}(this));

/*jslint browser: true, evil: false, plusplus: true */
/*global DVSA, $, console */

(function (globals) {
    'use strict';
    var init,
        searchSuggest,
        DVSA = globals.DVSA || {};

    //  GOVUK interface for twitter typeahead v.0.9.n
    //  Dependencies: jQuery, Twitter Typeahead
    //
    // - Defaults config options - ensuring consistent behaviour
    // - Loads JS source only when required (async)
    // - Allows upgrade path without impacting views

    searchSuggest = function searchSuggest(selector, dataSetID, prefetchURL,  options){

        // todo: pass 'onSelect' callback func

        this.sourceJS = '/public/javascripts/vendor/typeahead.min.js';
        this.selector = selector;
        this.dataSetID = dataSetID;
        this.prefetchURL = prefetchURL;

        var options = options || {};

        this.options = {
            "cache": options.cache || false,
            "minLength": options.minLength || 3,
            "limit": options.limit || 8
        };

        this.init();
    };

    searchSuggest.prototype.init = function(){

        if (typeof $.prototype.typeahead !== 'function' ){
            console.log('ext loading');
            this.loadJS(this.sourceJS, this.setup.bind(this));
        }else{
            console.log('already loaded');
            this.setup();
        }
    };

    searchSuggest.prototype.setup = function(){

        $(this.selector).typeahead([
            {
                name: this.dataSetID,
                prefetch: this.prefetchURL,
                cache: this.options.cache,
                minLength: this.options.minLength,
                limit: this.options.limit
            }
        ]);

    };

    searchSuggest.prototype.loadJS = function(src, cb){
        var ref = window.document.getElementsByTagName( "script" )[ 0 ];
        var script = window.document.createElement( "script" );

        script.src = src;

        script.async = true;
        ref.parentNode.insertBefore( script, ref );
        if (cb && typeof(cb) === "function") {
            script.onload = cb;
        }
        return script;
    };

    init = function () {};

    // public
    globals.DVSAModuleSearchSuggest = {
        init: init,
        searchSuggest: searchSuggest
    };
}(this));


/*jslint browser: true, evil: false, plusplus: true */
/*global DVSA, $, DVSASelectToggle */

(function (globals) {
    'use strict';
    // Add modules
    globals.DVSA.Modules.DVSASelectToggle = DVSASelectToggle;
    globals.DVSA.Modules.DVSACriteria = DVSACriteria;
    globals.DVSA.Modules.DVSACookiesEnabled = DVSACookiesEnabled;
    globals.DVSA.Modules.DVSAShowHideToggle = DVSAShowHideToggle;
    globals.DVSA.Modules.DVSAShowHideToggleClasses = DVSAShowHideToggleClasses;
    globals.DVSA.Modules.DVSAMarkRepairs = DVSAMarkRepairs;
    globals.DVSA.Modules.DoubleClickPrevention = DoubleClickPrevention;

    // Initialise
    globals.DVSA.init($({}));
}(this));
