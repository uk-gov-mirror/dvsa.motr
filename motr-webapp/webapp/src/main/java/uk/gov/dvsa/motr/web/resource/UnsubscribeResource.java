package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsService;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.analytics.SmartSurveyFeedback;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.UnsubscribeService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UnsubscribeConfirmationParams;
import uk.gov.dvsa.motr.web.formatting.MakeModelFormatter;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.UNSUBSCRIBE_FAILURE_KEY;
import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/unsubscribe")
@Produces("text/html")
public class UnsubscribeResource {

    private final UnsubscribeService unsubscribeService;
    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private final VehicleDetailsClient client;
    private final SmartSurveyFeedback smartSurveyFeedback;

    @Inject
    public UnsubscribeResource(
            UnsubscribeService unsubscribeService,
            TemplateEngine renderer,
            MotrSession motrSession,
            VehicleDetailsClient client,
            SmartSurveyFeedback smartSurveyFeedback
    ) {

        this.unsubscribeService = unsubscribeService;
        this.renderer = renderer;
        this.motrSession = motrSession;
        this.client = client;
        this.smartSurveyFeedback = smartSurveyFeedback;
    }

    @GET
    @Path("{unsubscribeId}")
    public String unsubscribeGet(@PathParam("unsubscribeId") String unsubscribeId) throws Exception {

        return unsubscribeService.findSubscriptionForUnsubscribe(unsubscribeId).map(subscription -> {

            UnsubscribeViewModel viewModel = populateViewModelFromSubscription(subscription);
            populateSmartSurvey(subscription);
            Map<String, Object> map = new HashMap<>();
            map.put("viewModel", viewModel);
            map.putAll(smartSurveyFeedback.formatAttributes());
            smartSurveyFeedback.clear();

            return renderer.render("unsubscribe", map);
        }).orElseGet(() -> {
            DataLayerHelper helper = new DataLayerHelper();
            helper.putAttribute(UNSUBSCRIBE_FAILURE_KEY, unsubscribeId);
            Map<String, Object> map = new HashMap<>();
            map.putAll(helper.formatAttributes());
            return renderer.render("unsubscribe-error", map);
        });
    }

    @POST
    @Path("{unsubscribeId}")
    public Response unsubscribePost(@PathParam("unsubscribeId") String unsubscribeId) throws Exception {

        Subscription removedSubscription = unsubscribeService.unsubscribe(unsubscribeId);

        UnsubscribeConfirmationParams params = new UnsubscribeConfirmationParams();
        params.setContact(removedSubscription.getContactDetail().getValue());
        params.setExpiryDate(removedSubscription.getMotDueDate().toString());
        params.setRegistration(removedSubscription.getVrm());
        params.setContactType(removedSubscription.getContactDetail().getContactType().getValue());

        motrSession.setUnsubscribeConfirmationParams(params);

        return redirect("unsubscribe/confirmed");
    }

    private UnsubscribeViewModel populateViewModelFromSubscription(Subscription subscription) {

        VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(subscription.getVrm(), client);

        return new UnsubscribeViewModel()
                .setEmail(subscription.getContactDetail().getValue())
                .setExpiryDate(subscription.getMotDueDate())
                .setRegistration(subscription.getVrm())
                .setMakeModel(MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails, ", "));
    }

    private void populateSmartSurvey(Subscription subscription) {

        VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(subscription.getVrm(), client);

        smartSurveyFeedback.addContactType(Subscription.ContactType.EMAIL.getValue());
        smartSurveyFeedback.addVrm(subscription.getVrm());
        smartSurveyFeedback.addVehicleType(subscription.getVehicleType());
        smartSurveyFeedback.addIsSigningBeforeFirstMotDue(vehicleDetails.hasNoMotYet());
    }
}
