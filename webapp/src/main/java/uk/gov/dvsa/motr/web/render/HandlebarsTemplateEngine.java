package uk.gov.dvsa.motr.web.render;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import org.apache.log4j.MDC;

import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import java.io.IOException;

import static uk.gov.dvsa.motr.web.system.SystemVariable.STATIC_ASSETS_HASH;
import static uk.gov.dvsa.motr.web.system.SystemVariable.STATIC_ASSETS_URL;

public class HandlebarsTemplateEngine implements TemplateEngine {

    private static final String ASSETS_HELPER_NAME = "asset";
    private static final String REQUEST_ID_HELPER_NAME = "requestId";

    private final Handlebars handlebars;

    public HandlebarsTemplateEngine(
            @SystemVariableParam(STATIC_ASSETS_HASH) String assetsRootPath,
            @SystemVariableParam(STATIC_ASSETS_URL) String assetsHash
    ) {

        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/template");
        loader.setSuffix(".hbs");
        // (1) - resource, (2) - version
        String assetsPathFormat = (assetsRootPath + "/%s?v=%s").replaceAll("(?<!(http:|https:))[//]+", "/");
        handlebars = new Handlebars(loader)
                .registerHelper(ASSETS_HELPER_NAME, assetsHelper(assetsPathFormat, assetsHash))
                .registerHelper(REQUEST_ID_HELPER_NAME, (context, options) -> MDC.get("AWSRequestId"))
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

    private static Helper<Object> assetsHelper(String assetsPathFormat, String assetsHash) {

        return (context, options) ->
                String.format(assetsPathFormat, options.param(0), assetsHash);
    }
}
