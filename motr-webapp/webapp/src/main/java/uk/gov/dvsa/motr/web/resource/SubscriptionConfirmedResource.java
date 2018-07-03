package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.conversion.DataAnonymizer;
import uk.gov.dvsa.motr.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.analytics.SmartSurveyHelper;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidConfirmationIdException;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyConfirmedException;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.service.SubscriptionConfirmationService;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.SubscriptionConfirmationParams;
import uk.gov.dvsa.motr.web.render.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.CONTACT_ID;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.CONTACT_TYPE;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.DLVA_ID_KEY;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.EVENT_TYPE;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.MOT_TEST_NUMBER_KEY;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;

import static java.util.Collections.emptyMap;


@Singleton
@Path("/confirm-subscription")
@Produces("text/html")
public class SubscriptionConfirmedResource {

    private static final String REPLY_PHONE_NUMBER = "07491163045";

    private final TemplateEngine renderer;
    private final DataLayerHelper dataLayerHelper;
    private final SmartSurveyHelper smartSurveyHelperSatisfaction;
    private final SmartSurveyHelper smartSurveyHelperFeedback;
    private final SubscriptionConfirmationService subscriptionConfirmationService;
    private final MotrSession motrSession;
    private final UrlHelper urlHelper;
    private final DataAnonymizer anonymizer;


    @Inject
    public SubscriptionConfirmedResource(
            TemplateEngine renderer,
            SubscriptionConfirmationService pendingSubscriptionActivatorService,
            MotrSession motrSession,
            UrlHelper urlHelper,
            DataAnonymizer anonymizer
    ) {

        this.renderer = renderer;
        this.subscriptionConfirmationService = pendingSubscriptionActivatorService;
        this.motrSession = motrSession;
        this.urlHelper = urlHelper;
        this.dataLayerHelper = new DataLayerHelper();
        this.anonymizer = anonymizer;
        this.smartSurveyHelperSatisfaction = new SmartSurveyHelper(
                "http://www.smartsurvey.co.uk/s/YN642/",
                "smartSurveySatisfaction"
        );
        this.smartSurveyHelperFeedback = new SmartSurveyHelper(
                "http://www.smartsurvey.co.uk/s/MKVXI/",
                "smartSurveyFeedback"
        );
    }

    @GET
    @Path("{confirmationId}")
    public Response confirmSubscriptionGet(@PathParam("confirmationId") String confirmationId) {

        try {
            Subscription subscription = subscriptionConfirmationService.confirmSubscription(confirmationId);
            setConfirmationSessionParams(subscription);
            return RedirectResponseBuilder.redirect(urlHelper.subscriptionConfirmedFirstTimeLink());
        } catch (SubscriptionAlreadyConfirmedException e) {

            Subscription existingSubscription = e.getExistingSubscription();
            setConfirmationSessionParams(existingSubscription);
            return RedirectResponseBuilder.redirect(urlHelper.subscriptionConfirmedNthTimeLink(
                    existingSubscription.getContactDetail().getContactType()));
        } catch (InvalidConfirmationIdException e) {

            return Response.ok(renderer.render("subscription-error", emptyMap())).build();
        }
    }

    @GET
    @Path("confirmed")
    public String confirmSubscriptionFirstTimeGet() {

        return showConfirmationPage();
    }

    @GET
    @Path("already-confirmed")
    public String confirmSubscriptionNthTimeGet() {

        return showConfirmationPage();
    }

    private String showConfirmationPage() {

        SubscriptionConfirmationParams subscription = motrSession.getSubscriptionConfirmationParams();

        Map<String, Object> modelMap = new HashMap<>();

        if (null != subscription) {
            dataLayerHelper.putAttribute(VRM_KEY, subscription.getRegistration());
            dataLayerHelper.putAttribute(DataLayerHelper.VEHICLE_DATA_ORIGIN_KEY, String.valueOf(subscription.getVehicleType()));
            dataLayerHelper.putAttribute(EVENT_TYPE, "subscribe");
            dataLayerHelper.putAttribute(CONTACT_TYPE, subscription.getContactType());
            dataLayerHelper.putAttribute(CONTACT_ID, anonymizer.anonymizeContactData(subscription.getContact()));
            smartSurveyHelperFeedback.putAttribute(SmartSurveyHelper.CONTACT_TYPE, subscription.getContactType());
            smartSurveyHelperSatisfaction.putAttribute(SmartSurveyHelper.CONTACT_TYPE, subscription.getContactType());
            modelMap.put("isMotVehicle", isMotVehicle(subscription));

            if (subscription.getContactType().equals(Subscription.ContactType.MOBILE.getValue())) {
                modelMap.put("usingSms", true);
                modelMap.put("replyNumber", REPLY_PHONE_NUMBER);
                modelMap.put("registration", subscription.getRegistration());
            }

            if (subscription.getMotTestNumber() != null && !subscription.getMotTestNumber().isEmpty()) {
                dataLayerHelper.putAttribute(MOT_TEST_NUMBER_KEY, subscription.getMotTestNumber());
            } else {
                dataLayerHelper.putAttribute(DLVA_ID_KEY, subscription.getDvlaId());
            }
            return renderer.render("subscription-confirmation", buildMap(modelMap));
        } else {
            return renderer.render("subscription-error", emptyMap());
        }
    }

    private Map<String, Object> buildMap(Map<String, Object> map) {

        map.putAll(dataLayerHelper.formatAttributes());
        map.putAll(smartSurveyHelperFeedback.formatAttributes());
        map.putAll(smartSurveyHelperSatisfaction.formatAttributes());
        dataLayerHelper.clear();
        smartSurveyHelperFeedback.clear();
        smartSurveyHelperSatisfaction.clear();
        motrSession.clear();

        return map;
    }

    private void setConfirmationSessionParams(Subscription subscription) {

        motrSession.clear();

        MotIdentification motIdentification = subscription.getMotIdentification();
        SubscriptionConfirmationParams params = new SubscriptionConfirmationParams();
        params.setRegistration(subscription.getVrm());
        params.setDvlaId(motIdentification.getDvlaId().orElse(""));
        params.setMotTestNumber(motIdentification.getMotTestNumber().orElse(""));
        params.setContactType(subscription.getContactDetail().getContactType().getValue());
        params.setContact(subscription.getContactDetail().getValue());
        params.setVehicleType(subscription.getVehicleType());
        motrSession.setSubscriptionConfirmationParams(params);
    }

    private boolean isMotVehicle(SubscriptionConfirmationParams subscription) {
        return subscription.getVehicleType() == VehicleType.MOT;
    }
}
