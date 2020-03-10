package uk.gov.dvsa.motr.web.cookie;

import javax.inject.Singleton;

@Singleton
public class UserCookiePreferences {

    private boolean isPresent;
    private boolean analytics;

    public boolean isPresent() {
        return this.isPresent;
    }

    public UserCookiePreferences setIsPresent(boolean isPresent) {
        this.isPresent = isPresent;
        return this;
    }

    public boolean shouldUseAnalytics() {
        return this.analytics;
    }

    public UserCookiePreferences setShouldUseAnalytics(boolean shouldUseAnalytics) {
        this.analytics = shouldUseAnalytics;
        return this;
    }

    public void clear() {
        this.setIsPresent(false).setShouldUseAnalytics(false);
    }

}