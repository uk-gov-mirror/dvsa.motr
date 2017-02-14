package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.SubscriptionConfirmationViewModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Singleton
@Path("/subscription-confirmation")
@Produces("text/html")
public class SubscriptionConfirmationResource {

    private final TemplateEngine renderer;

    @Inject
    public SubscriptionConfirmationResource(TemplateEngine renderer) {

        this.renderer = renderer;
    }

    @GET
    public String subscriptionConfirmationGet() {

        Map<String, Object> map = new HashMap<>();
        SubscriptionConfirmationViewModel subscriptionConfirmationViewModel = new SubscriptionConfirmationViewModel();

        // TODO remove hard coded values and implement details from session/vehicle client
        subscriptionConfirmationViewModel.setVrm("TEST-REG")
                .setExpiryDate(LocalDate.now())
                .setEmail("test-email@email.com");

        map.put("viewModel", subscriptionConfirmationViewModel);

        return renderer.render("subscription-confirmation", map);
    }
}
