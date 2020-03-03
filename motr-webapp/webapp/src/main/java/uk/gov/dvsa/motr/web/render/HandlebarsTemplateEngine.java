package uk.gov.dvsa.motr.web.render;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.web.cookie.UserCookiePreferences;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.dvsa.motr.web.render.helper.ShouldUseAnalyticsHelper;
import uk.gov.dvsa.motr.web.render.helper.UserHasSetCookiePreferencesHelper;

import java.io.IOException;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;
import static uk.gov.dvsa.motr.web.system.SystemVariable.RELEASE_VERSION;
import static uk.gov.dvsa.motr.web.system.SystemVariable.STATIC_ASSETS_HASH;
import static uk.gov.dvsa.motr.web.system.SystemVariable.STATIC_ASSETS_URL;

public class HandlebarsTemplateEngine implements TemplateEngine {

    private static final Logger logger = LoggerFactory.getLogger(HandlebarsTemplateEngine.class);

    private static final String ASSETS_HELPER_NAME = "asset";
    private static final String REQUEST_ID_HELPER_NAME = "requestId";
    private static final String URL_HELPER = "url";
    private static final String RELEASE_VERSION_HELPER = "release-version";
    private static final String ANALYTICS_ALLOWED_HELPER = "shouldUseAnalytics";
    private static final String USER_COOKIE_PREFERENCES_SET_HELPER = "userHasSetCookiePreferences";

    private final Handlebars handlebars;

    @Inject
    public HandlebarsTemplateEngine(
            @SystemVariableParam(STATIC_ASSETS_URL) String assetsRootPath,
            @SystemVariableParam(STATIC_ASSETS_HASH) String assetsHash,
            @SystemVariableParam(BASE_URL) String baseUrl,
            @SystemVariableParam(RELEASE_VERSION) String releaseVersion,
            UserCookiePreferences userCookiePreferences
    ) {

        ShouldUseAnalyticsHelper analyticsHelper = new ShouldUseAnalyticsHelper(userCookiePreferences);
        UserHasSetCookiePreferencesHelper userHasSetCookiePreferencesHelper =
                new UserHasSetCookiePreferencesHelper(userCookiePreferences);

        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/template");
        loader.setSuffix(".hbs");
        // (1) - resource, (2) - version
        String assetsPathFormat = assetsRootPath + "/%s?v=%s";
        handlebars = new Handlebars(loader)
                .registerHelper(ASSETS_HELPER_NAME, assetsHelper(assetsPathFormat, assetsHash))
                .registerHelper(REQUEST_ID_HELPER_NAME, (context, options) -> MDC.get("AWSRequestId"))
                .registerHelper(URL_HELPER, urlHelper(baseUrl))
                .registerHelper(RELEASE_VERSION_HELPER, (context, options) -> releaseVersion)
                .registerHelper(ANALYTICS_ALLOWED_HELPER, analyticsHelper)
                .registerHelper(USER_COOKIE_PREFERENCES_SET_HELPER, userHasSetCookiePreferencesHelper)
                .with(new ConcurrentMapTemplateCache());
    }

    @Override
    public String render(String templateName, Object context) {

        try {
            return handlebars.compile(templateName).apply(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void precompile(String templateName) {

        try {
            logger.debug("precompiling: {}", templateName);
            handlebars.compile(templateName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Helper<Object> assetsHelper(String assetsPathFormat, String assetsHash) {

        return (context, options) ->
                normalizeSlashes(String.format(assetsPathFormat, options.param(0), assetsHash));
    }

    private static Helper<Object> urlHelper(String baseUrl) {

        return (context, options) ->
                normalizeSlashes(String.format("%s/%s", baseUrl, options.param(0)));
    }

    private static String normalizeSlashes(String input) {

        return input.replaceAll("(?<!(http:|https:))[//]+", "/");
    }
}
