package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/accessibility-statement")
@Produces("text/html")
public class AccessibilityStatementResource {

    private static final String ACCESSIBILITY_STATEMENT_TEMPLATE_NAME = "accessibility-statement";

    private final TemplateEngine renderer;

    @Inject
    public AccessibilityStatementResource(TemplateEngine renderer) {

        this.renderer = renderer;
    }

    @GET
    public String termsPage() throws Exception {

        return renderer.render(ACCESSIBILITY_STATEMENT_TEMPLATE_NAME, emptyMap());
    }

}
