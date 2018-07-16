package uk.gov.dvsa.motr.web.resource;

import org.apache.commons.lang3.StringUtils;

import uk.gov.dvsa.motr.conversion.DataAnonymizer;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsService;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.analytics.SmartSurveyFeedback;
import uk.gov.dvsa.motr.web.analytics.SmartSurveySatisfactionUnsubscribe;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UnsubscribeConfirmationParams;
import uk.gov.dvsa.motr.web.formatting.MakeModelFormatter;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.CONTACT_ID;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.CONTACT_TYPE;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.EVENT_TYPE;
import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;

@Singleton
@Path("/unsubscribe")
@Produces("text/html")
public class UnsubscribeConfirmedResource {

    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private final VehicleDetailsClient client;
    private final DataAnonymizer anonymizer;
    private final SmartSurveySatisfactionUnsubscribe smartSurveyHelperSatisfaction;
    private final SmartSurveyFeedback smartSurveyHelperFeedback;

    @Inject
    public UnsubscribeConfirmedResource(
            TemplateEngine renderer,
            MotrSession motrSession,
            VehicleDetailsClient client,
            DataAnonymizer anonymizer,
            SmartSurveyFeedback smartSurveyHelperFeedback,
            SmartSurveySatisfactionUnsubscribe smartSurveyHelperSatisfaction
    ) {

        this.renderer = renderer;
        this.motrSession = motrSession;
        this.client = client;
        this.anonymizer = anonymizer;
        this.smartSurveyHelperSatisfaction = smartSurveyHelperSatisfaction;
        this.smartSurveyHelperFeedback = smartSurveyHelperFeedback;
    }

    @GET
    @Path("confirmed")
    public String unsubscribeConfirmed() throws Exception {

        UnsubscribeConfirmationParams params = motrSession.getUnsubscribeConfirmationParams();
        if (params == null) {
            throw new NotFoundException();
        }

        VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(params.getRegistration(), client);

        DataLayerHelper helper = new DataLayerHelper();
        helper.putAttribute(VRM_KEY, params.getRegistration());
        helper.setVehicleDataOrigin(vehicleDetails);
        helper.putAttribute(EVENT_TYPE, "unsubscribe");
        helper.putAttribute(CONTACT_TYPE, params.getContactType());
        helper.putAttribute(CONTACT_ID, anonymizer.anonymizeContactData(params.getContact()));

        smartSurveyHelperFeedback.addContactType(params.getContactType());
        smartSurveyHelperFeedback.addVrm(params.getRegistration());
        smartSurveyHelperFeedback.addIsSigningBeforeFirstMotDue(StringUtils.isEmpty(params.getMotTestNumber()));
        smartSurveyHelperSatisfaction.addContactType(params.getContactType());
        smartSurveyHelperSatisfaction.addVrm(params.getRegistration());
        smartSurveyHelperSatisfaction.addIsSigningBeforeFirstMotDue(StringUtils.isEmpty(params.getMotTestNumber()));

        if (vehicleDetails.getVehicleType() != null) {
            smartSurveyHelperFeedback.addVehicleType(vehicleDetails.getVehicleType());
            smartSurveyHelperSatisfaction.addVehicleType(vehicleDetails.getVehicleType());
        }

        UnsubscribeViewModel viewModel = new UnsubscribeViewModel()
                .setRegistration(params.getRegistration())
                .setExpiryDate(LocalDate.parse(params.getExpiryDate()))
                .setEmail(params.getContact())
                .setMakeModel(MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails, ", "));

        Map<String, Object> map = new HashMap<>();
        map.putAll(helper.formatAttributes());
        map.putAll(smartSurveyHelperFeedback.formatAttributes());
        map.putAll(smartSurveyHelperSatisfaction.formatAttributes());
        map.put("viewModel", viewModel);
        smartSurveyHelperFeedback.clear();
        smartSurveyHelperSatisfaction.clear();
        return renderer.render("unsubscribe-confirmation", map);
    }
}
