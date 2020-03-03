package uk.gov.dvsa.motr.web.cookie;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class UserCookiePreferencesFilterTest {

    UserCookiePreferencesFilter userCookiePreferencesFilter;
    UserCookiePreferences userCookiePreferences;

    @Before
    public void setup() {
        this.userCookiePreferences = new UserCookiePreferences();
        this.userCookiePreferencesFilter = new UserCookiePreferencesFilter(this.userCookiePreferences);
    }

    @DataProvider
    public static Object[][] userPreferenceCookieDataProvider() {
        return new Object[][] {
                {UserCookiePreferencesFilter.USER_COOKIE_PREFERENCE_VALUE_ON, true},  // 0: Analytics is on, expect true
                {UserCookiePreferencesFilter.USER_COOKIE_PREFERENCE_VALUE_OFF, false} // 1: Analytics is off, expect false
        };
    }

    @UseDataProvider("userPreferenceCookieDataProvider")
    @Test
    public void whenUserPreferenceCookieExistsUserCookiePreferencesIsSet(String analytics, boolean expected) {

        HashMap<String, Cookie> cookies = new HashMap<>();
        cookies.put(
                UserCookiePreferencesFilter.USER_PREFERENCE_COOKIE_NAME,
                new Cookie(
                        UserCookiePreferencesFilter.USER_PREFERENCE_COOKIE_NAME,
                        this.generateUserCookiePreferencesCookieValue(analytics)
                )
        );

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getCookies()).thenReturn(cookies);

        userCookiePreferencesFilter.filter(containerRequestContext);

        Assert.assertTrue(this.userCookiePreferences.isPresent());
        Assert.assertEquals(expected, this.userCookiePreferences.shouldUseAnalytics());
    }

    @Test
    public void whenUserPreferenceCookieNotExistsUserCookiePreferenceNotSetAndNonComplianceAssumed() {
        HashMap<String, Cookie> cookies = new HashMap<>();

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getCookies()).thenReturn(cookies);

        userCookiePreferencesFilter.filter(containerRequestContext);

        Assert.assertFalse(this.userCookiePreferences.isPresent());
        Assert.assertFalse(this.userCookiePreferences.shouldUseAnalytics());
    }

    @Test
    public void whenUserPreferenceCookieExistsButDoesNotContainAnalyticsPreferenceNonComplianceAssumed() {
        HashMap<String, Cookie> cookies = new HashMap<>();
        cookies.put(
                UserCookiePreferencesFilter.USER_PREFERENCE_COOKIE_NAME,
                new Cookie(
                        UserCookiePreferencesFilter.USER_PREFERENCE_COOKIE_NAME,
                        "{\"random_key\": \"with_value\"}"
                )
        );

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getCookies()).thenReturn(cookies);

        userCookiePreferencesFilter.filter(containerRequestContext);

        Assert.assertTrue(this.userCookiePreferences.isPresent());
        Assert.assertFalse(this.userCookiePreferences.shouldUseAnalytics());
    }

    @Test
    public void whenUserPreferenceCookieExistsButJsonCannotBeDecodedNonComplianceAssumed() {
        HashMap<String, Cookie> cookies = new HashMap<>();
        cookies.put(
                UserCookiePreferencesFilter.USER_PREFERENCE_COOKIE_NAME,
                new Cookie(
                        UserCookiePreferencesFilter.USER_PREFERENCE_COOKIE_NAME,
                        "{someinvalidjson}"
                )
        );

        ContainerRequestContext containerRequestContext = mock(ContainerRequestContext.class);
        when(containerRequestContext.getCookies()).thenReturn(cookies);

        userCookiePreferencesFilter.filter(containerRequestContext);

        Assert.assertTrue(this.userCookiePreferences.isPresent());
        Assert.assertFalse(this.userCookiePreferences.shouldUseAnalytics());
    }

    private String generateUserCookiePreferencesCookieValue(String analytics) {
        JSONObject jsonObject = new JSONObject();

        boolean yeah = analytics.equals(UserCookiePreferencesFilter.USER_COOKIE_PREFERENCE_VALUE_ON);

        jsonObject.put(
                UserCookiePreferencesFilter.USER_COOKIE_PREFERENCE_KEY_ANALYTICS,
                analytics
        );

        return jsonObject.toString();
    }
}