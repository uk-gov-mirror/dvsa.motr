package uk.gov.dvsa.motr.web.render.helper;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.UserCookiePreferences;

import java.io.IOException;

public class ShouldUseAnalyticsHelperTest {

    UserCookiePreferences userCookiePreferences;
    Handlebars handlebars;

    @Before
    public void setup() {
        this.userCookiePreferences = new UserCookiePreferences();
        this.handlebars = new Handlebars();
    }

    @Test
    public void userHasNotSetCookiePreferencesIsNull() throws IOException {
        ShouldUseAnalyticsHelper helper = new ShouldUseAnalyticsHelper(null);
        String result = this.getTemplateResult(helper);
        Assert.assertEquals("false", result);
    }

    @Test
    public void userHasNotSetCookiePreferences() throws IOException {
        this.userCookiePreferences.setIsPresent(false);
        ShouldUseAnalyticsHelper helper = new ShouldUseAnalyticsHelper(this.userCookiePreferences);
        String result = this.getTemplateResult(helper);
        Assert.assertEquals("false", result);
    }

    @Test
    public void userHasSetCookiePreferencesWithAnalyticsOn() throws IOException {
        this.userCookiePreferences.setIsPresent(true);
        this.userCookiePreferences.setShouldUseAnalytics(true);
        ShouldUseAnalyticsHelper helper = new ShouldUseAnalyticsHelper(this.userCookiePreferences);
        String result = this.getTemplateResult(helper);
        Assert.assertEquals("true", result);
    }

    @Test
    public void userHasSetCookiePreferencesWithAnalyticsOff() throws IOException {
        this.userCookiePreferences.setIsPresent(true);
        this.userCookiePreferences.setShouldUseAnalytics(false);
        ShouldUseAnalyticsHelper helper = new ShouldUseAnalyticsHelper(this.userCookiePreferences);
        String result = this.getTemplateResult(helper);
        Assert.assertEquals("false", result);
    }

    private String getTemplateResult(ShouldUseAnalyticsHelper helper) throws IOException {
        this.handlebars.registerHelper("sua", helper);
        Template template = this.handlebars.compileInline("{{#sua}}true{{else}}false{{/sua}}");
        return template.apply(null);
    }
}