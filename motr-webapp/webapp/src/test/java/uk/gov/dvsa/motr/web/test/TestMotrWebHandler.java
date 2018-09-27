package uk.gov.dvsa.motr.web.test;

import uk.gov.dvsa.motr.web.cookie.CookieCipher;
import uk.gov.dvsa.motr.web.handler.MotrWebHandler;
import uk.gov.dvsa.motr.web.system.binder.ConfigBinder;

public class TestMotrWebHandler extends MotrWebHandler {

    public TestMotrWebHandler() {

        super();

        ConfigBinder binder = new ConfigBinder() {

            @Override
            protected void configure() {
                bindFactory(MockCookieCipherFactory.class).to(CookieCipher.class).ranked(100);
            }
        };
        application.register(binder);
    }
}
