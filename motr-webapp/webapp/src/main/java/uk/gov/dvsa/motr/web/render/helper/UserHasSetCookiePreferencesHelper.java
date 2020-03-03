package uk.gov.dvsa.motr.web.render.helper;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import uk.gov.dvsa.motr.web.cookie.UserCookiePreferences;

import java.io.IOException;

public class UserHasSetCookiePreferencesHelper implements Helper<Object> {

    private UserCookiePreferences userCookiePreferences;

    public UserHasSetCookiePreferencesHelper(UserCookiePreferences userCookiePreferences) {
        this.userCookiePreferences = userCookiePreferences;
    }

    @Override
    public Object apply(Object context, Options options) throws IOException {
        if (this.userCookiePreferences != null && this.userCookiePreferences.isPresent()) {
            return options.fn(context);
        }
        return options.inverse(context);
    }
}
