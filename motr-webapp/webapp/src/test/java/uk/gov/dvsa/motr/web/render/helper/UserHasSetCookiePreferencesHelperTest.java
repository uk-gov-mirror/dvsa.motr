package uk.gov.dvsa.motr.web.render.helper;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.UserCookiePreferences;

import java.io.IOException;

public class UserHasSetCookiePreferencesHelperTest {

    UserCookiePreferences userCookiePreferences;
    Handlebars handlebars;

    @Before
    public void setup() {
        this.userCookiePreferences = new UserCookiePreferences();
        this.handlebars = new Handlebars();
    }

    @Test
    public void userHasNotSetCookiePreferencesIsNull() throws IOException {
        UserHasSetCookiePreferencesHelper helper = new UserHasSetCookiePreferencesHelper(null);
        String result = this.getTemplateResult(helper);
        Assert.assertEquals("false", result);
    }

    @Test
    public void userHasNotSetCookiePreferences() throws IOException {
        this.userCookiePreferences.setIsPresent(false);
        UserHasSetCookiePreferencesHelper helper = new UserHasSetCookiePreferencesHelper(this.userCookiePreferences);
        String result = this.getTemplateResult(helper);
        Assert.assertEquals("false", result);
    }

    @Test
    public void userHasSetCookiePreferences() throws IOException {
        this.userCookiePreferences.setIsPresent(true);
        UserHasSetCookiePreferencesHelper helper = new UserHasSetCookiePreferencesHelper(this.userCookiePreferences);
        String result = this.getTemplateResult(helper);
        Assert.assertEquals("true", result);
    }

    private String getTemplateResult(UserHasSetCookiePreferencesHelper helper) throws IOException {
        this.handlebars.registerHelper("ucps", helper);
        Template template = this.handlebars.compileInline("{{#ucps}}true{{else}}false{{/ucps}}");
        return template.apply(null);
    }
}