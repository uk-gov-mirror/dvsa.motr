package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.SubscriptionConfirmationViewModel;

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

import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/subscription-confirmation")
@Produces("text/html")
public class SubscriptionConfirmationResource {

    private final TemplateEngine renderer;
    private final VehicleDetailsClient vehicleDetailsClient;
    private final MotrSession motrSession;

    @Inject
    public SubscriptionConfirmationResource(
            VehicleDetailsClient vehicleDetailsClient,
            TemplateEngine renderer,
            MotrSession motrSession
    ) {

        this.vehicleDetailsClient = vehicleDetailsClient;
        this.renderer = renderer;
        this.motrSession = motrSession;
    }

    @GET
    public Response subscriptionConfirmationGet() throws VehicleDetailsClientException, URISyntaxException {

        if (!this.motrSession.isAllowedOnPage()) {
            return redirect("/");
        }

        Map<String, Object> map = new HashMap<>();
        SubscriptionConfirmationViewModel subscriptionConfirmationViewModel = new SubscriptionConfirmationViewModel();

        Optional<VehicleDetails> vehicleOptional = this.vehicleDetailsClient.fetch(this.motrSession.getVrmFromSession());

        if (vehicleOptional.isPresent()) {
            VehicleDetails vehicle = vehicleOptional.get();
            subscriptionConfirmationViewModel.setVrm(this.motrSession.getVrmFromSession())
                    .setExpiryDate(vehicle.getMotExpiryDate())
                    .setEmail(this.motrSession.getEmailFromSession());
        } else {
            throw new NotFoundException();
        }

        map.put("viewModel", subscriptionConfirmationViewModel);

        return Response.ok(renderer.render("subscription-confirmation", map)).build();
    }
}
