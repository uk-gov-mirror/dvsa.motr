package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/cookie-preferences")
@Produces("text/html")
public class CookiePreferencesResource {

    private static final String COOKIES_TEMPLATE_NAME = "cookie-preferences";

    private final TemplateEngine renderer;

    @Inject
    public CookiePreferencesResource(TemplateEngine renderer) {

        this.renderer = renderer;
    }

    @GET
    public String cookiesPage() {

        return renderer.render(COOKIES_TEMPLATE_NAME, emptyMap());
    }
}
