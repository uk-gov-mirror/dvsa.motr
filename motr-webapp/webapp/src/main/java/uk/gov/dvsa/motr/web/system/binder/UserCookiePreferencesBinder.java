package uk.gov.dvsa.motr.web.system.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UserCookiePreferences;
import uk.gov.dvsa.motr.web.cookie.UserCookiePreferencesFilter;

import javax.inject.Singleton;

public class UserCookiePreferencesBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bind(UserCookiePreferences.class).to(UserCookiePreferences.class).in(Singleton.class);
    }
}
