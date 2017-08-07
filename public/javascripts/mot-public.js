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

/*
    js module test
*/
(function (globals) {
    'use strict';

    var init,
        jsTest,
        JSTest,
        DVSA = globals.DVSA || {};

    JSTest = function JSTest() {};

    JSTest.prototype.init = function(){

        function test () {
            console.log('js test');
        }
    };

    globals.DVSATest = {
        jsTest: JSTest
    };

}(this));

/*jslint browser: true, evil: false, plusplus: true */
/*global DVSA, $, DVSASelectToggle */

(function (globals) {
    'use strict';
    // Add modules
    // globals.DVSA.Modules.DVSASelectToggle = DVSASelectToggle;
    globals.DVSA.Modules.DVSATest = DVSATest;

    // Initialise
    globals.DVSA.init($({}));
}(this));
