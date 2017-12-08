package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/terms-and-conditions")
@Produces("text/html")
public class TermsAndConditionsResource {

    private static final String TERMS_AND_CONDITIONS_TEMPLATE_NAME = "terms-and-conditions";

    private final TemplateEngine renderer;

    @Inject
    public TermsAndConditionsResource(TemplateEngine renderer) {

        this.renderer = renderer;
    }

    @GET
    public String termsPage() throws Exception {

        return renderer.render(TERMS_AND_CONDITIONS_TEMPLATE_NAME, emptyMap());
    }
}