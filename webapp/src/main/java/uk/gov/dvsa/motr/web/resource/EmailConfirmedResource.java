package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionConfirmationService;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.EmailConfirmedViewModel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/confirm-email")
@Produces("text/html")
public class EmailConfirmedResource {

    private final TemplateEngine renderer;
    private final DataLayerHelper dataLayerHelper;
    private SubscriptionConfirmationService subscriptionConfirmationService;

    @Inject
    public EmailConfirmedResource(
            TemplateEngine renderer,
            SubscriptionConfirmationService pendingSubscriptionActivatorService
    ) {

        this.renderer = renderer;
        this.subscriptionConfirmationService = pendingSubscriptionActivatorService;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    @Path("{confirmationId}")
    public String confirmEmailGet(@PathParam("confirmationId") String confirmationId) {

        try {
            Subscription subscription = subscriptionConfirmationService.confirmSubscription(confirmationId);
            dataLayerHelper.putAttribute(VRM_KEY, subscription.getVrm());

            return renderer.render("subscription-confirmation", buildViewModel(subscription));
        } catch (InvalidConfirmationIdException e) {
            return renderer.render("subscription-error", emptyMap());
        }
    }

    private Map<String, Object> buildViewModel(Subscription subscription) {

        Map<String, Object> map = new HashMap<>();
        EmailConfirmedViewModel viewModel = new EmailConfirmedViewModel();

        map.putAll(dataLayerHelper.formatAttributes());
        map.put("viewModel", viewModel
                .setEmail(subscription.getEmail())
                .setExpiryDate(subscription.getMotDueDate())
                .setRegistration(subscription.getVrm()));

        return map;
    }
}
