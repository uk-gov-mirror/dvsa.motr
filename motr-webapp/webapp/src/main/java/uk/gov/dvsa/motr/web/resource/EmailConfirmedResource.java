package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Singleton
@Path("/confirm-email")
@Produces("text/html")
public class EmailConfirmedResource {
    private final UrlHelper urlHelper;

    @Inject
    public EmailConfirmedResource(UrlHelper urlHelper) {

        this.urlHelper = urlHelper;
    }

    /*
    * This is required for any users that have a confirmation email sent before SMS went live, but have not yet confirmed.
     */
    @GET
    @Path("{confirmationId}")
    public Response confirmSubscriptionGet(@PathParam("confirmationId") String confirmationId) {

        return RedirectResponseBuilder.redirect(urlHelper.confirmSubscriptionLink(confirmationId));
    }
}
