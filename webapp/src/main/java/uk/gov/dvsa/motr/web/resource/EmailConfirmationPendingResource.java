package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/email-confirmation-pending")
@Produces("text/html")
public class EmailConfirmationPendingResource {

    private final TemplateEngine renderer;

    @Inject
    public EmailConfirmationPendingResource(
            TemplateEngine renderer
    ) {

        this.renderer = renderer;
    }

    @GET
    public String confirmEmailGet() {

        return renderer.render("email-confirmation-pending", emptyMap());
    }
}
