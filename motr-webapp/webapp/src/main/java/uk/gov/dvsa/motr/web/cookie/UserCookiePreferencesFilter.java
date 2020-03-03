package uk.gov.dvsa.motr.web.cookie;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.eventlog.session.SessionMalformedEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;

@Provider
public class UserCookiePreferencesFilter implements ContainerResponseFilter, ContainerRequestFilter {

    public static final String USER_PREFERENCE_COOKIE_NAME = "cm-user-preferences";
    public static final String USER_COOKIE_PREFERENCE_REQUEST_ATTRIBUTE = "user_cookie_preference";
    public static final String USER_COOKIE_PREFERENCE_KEY_ANALYTICS = "analytics";

    public static final String USER_COOKIE_PREFERENCE_VALUE_ON = "on";
    public static final String USER_COOKIE_PREFERENCE_VALUE_OFF = "off";

    private static final Logger logger = LoggerFactory.getLogger(UserCookiePreferencesFilter.class);

    private UserCookiePreferences userCookiePreferences;

    @Inject
    public UserCookiePreferencesFilter(UserCookiePreferences userCookiePreferences) {
        this.userCookiePreferences = userCookiePreferences;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            getUserCookiePreferencesFromCookie(requestContext);
        } catch (Exception e) {
            EventLogger.logErrorEvent(new SessionMalformedEvent(), e);
            throw new NotFoundException();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        getUserCookiePreferencesFromCookie(requestContext);
    }

    private void getUserCookiePreferencesFromCookie(ContainerRequestContext requestContext) {

        this.userCookiePreferences.clear();

        Cookie userCookiePreferenceCookie = getUserCookiePreferencesCookie(requestContext);

        if (userCookiePreferenceCookie == null) {
            logger.debug("Could not find user preference cookie; assuming non-consent.");
            this.userCookiePreferences.setIsPresent(false);
            this.userCookiePreferences.setShouldUseAnalytics(false);
        } else {
            this.decodeUserCookiePreferenceCookie(userCookiePreferenceCookie);
            this.userCookiePreferences.setIsPresent(true);
        }

        requestContext.setProperty(USER_COOKIE_PREFERENCE_REQUEST_ATTRIBUTE, this.userCookiePreferences);
    }

    private Cookie getUserCookiePreferencesCookie(ContainerRequestContext requestContext) {

        Map<String, Cookie> cookies = requestContext.getCookies();
        if (cookies != null) {
            return cookies.get(USER_PREFERENCE_COOKIE_NAME);
        }
        return null;
    }

    private void decodeUserCookiePreferenceCookie(Cookie userPreferenceCookie) {
        try {

            String decodedCookieValue =
                    URLDecoder.decode(userPreferenceCookie.getValue(), String.valueOf(StandardCharsets.UTF_8));

            JSONObject jsonObject = new JSONObject(decodedCookieValue);
            this.userCookiePreferences.setShouldUseAnalytics(
                    convertToBool(
                            jsonObject.optString(
                                    USER_COOKIE_PREFERENCE_KEY_ANALYTICS,
                                    USER_COOKIE_PREFERENCE_VALUE_OFF
                            )
                    )
            );
            logger.debug(
                    "Cookie preferences found and set; {} = {}",
                    USER_COOKIE_PREFERENCE_KEY_ANALYTICS,
                    this.userCookiePreferences.shouldUseAnalytics()
            );
        } catch (JSONException | UnsupportedEncodingException e) {
            logger.warn("Unable to decode user preference cookie value as JSON. Assuming non-consent.", e);
            this.userCookiePreferences.setShouldUseAnalytics(false);
        }
    }

    private static boolean convertToBool(String value) {
        return value.equals(USER_COOKIE_PREFERENCE_VALUE_ON);
    }
}
