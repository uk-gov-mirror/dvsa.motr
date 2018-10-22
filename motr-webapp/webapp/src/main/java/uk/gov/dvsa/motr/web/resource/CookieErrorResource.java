package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/cookie-error")
@Produces("text/html")
public class CookieErrorResource {

    private final TemplateEngine renderer;
    private final MotrSession session;

    @Inject
    public CookieErrorResource(TemplateEngine renderer, MotrSession session) {

        this.renderer = renderer;
        this.session = session;
    }

    @GET
    public String cookieErrorPage() throws Exception {

        this.session.setShouldClearCookies(true);
        return renderer.render("error/cookie-error", emptyMap());
    }
}
