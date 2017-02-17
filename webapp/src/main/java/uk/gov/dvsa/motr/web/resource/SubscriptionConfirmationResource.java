package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.system.SystemVariable;
import uk.gov.dvsa.motr.web.viewmodel.SubscriptionConfirmationViewModel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Singleton
@Path("/subscription-confirmation")
@Produces("text/html")
public class SubscriptionConfirmationResource {

    private final TemplateEngine renderer;
    private final VehicleDetailsClient vehicleDetailsClient;
    private final String baseUrl;
    private final MotrSession motrSession;

    @Inject
    public SubscriptionConfirmationResource(
            @SystemVariableParam(SystemVariable.BASE_URL) String baseUrl,
            VehicleDetailsClient vehicleDetailsClient,
            TemplateEngine renderer,
            MotrSession motrSession
    ) {

        this.baseUrl = baseUrl;
        this.vehicleDetailsClient = vehicleDetailsClient;
        this.renderer = renderer;
        this.motrSession = motrSession;
    }

    @GET
    public Response subscriptionConfirmationGet() throws VehicleDetailsClientException, URISyntaxException {

        if (!this.motrSession.isAllowedOnPage()) {
            return Response.status(Response.Status.FOUND).location(getFullUriForPage("/")).build();
        }

        Map<String, Object> map = new HashMap<>();
        SubscriptionConfirmationViewModel subscriptionConfirmationViewModel = new SubscriptionConfirmationViewModel();

        Optional<VehicleDetails> vehicleOptional = this.vehicleDetailsClient.fetch(this.motrSession.getRegNumberFromSession());

        if (vehicleOptional.isPresent()) {
            VehicleDetails vehicle = vehicleOptional.get();
            subscriptionConfirmationViewModel.setVrm(this.motrSession.getRegNumberFromSession())
                    .setExpiryDate(vehicle.getMotExpiryDate())
                    .setEmail(this.motrSession.getEmailFromSession());
        } else {
            throw new NotFoundException();
        }

        map.put("viewModel", subscriptionConfirmationViewModel);

        return Response.ok().entity(renderer.render("subscription-confirmation", map)).build();
    }

    private URI getFullUriForPage(String page) throws URISyntaxException {

        return UriBuilder.fromUri(new URI(this.baseUrl)).path(page).build();
    }
}
