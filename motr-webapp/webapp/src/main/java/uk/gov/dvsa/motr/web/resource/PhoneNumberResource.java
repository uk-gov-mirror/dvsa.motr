package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.formatting.PhoneNumberFormatter;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.PhoneNumberValidator;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.ERROR_KEY;
import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/phone-number")
@Produces("text/html")
public class PhoneNumberResource {

    private static final String PHONE_NUMBER_TEMPLATE = "phone-number";
    private static final String PHONE_NUMBER_MODEL_KEY = "phoneNumber";
    private static final String MESSAGE_MODEL_KEY = "message";
    private static final String MESSAGE_AT_FIELD_MODEL_KEY = "messageAtField";
    private static final String INPUT_FIELD_ID = "phone-number-input";
    private static final String INPUT_FIELD_ID_MODEL_KEY = "inputFieldId";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private PhoneNumberValidator validator;

    private DataLayerHelper dataLayerHelper;

    @Inject
    public PhoneNumberResource(
            MotrSession motrSession,
            TemplateEngine renderer,
            PhoneNumberValidator validator
    ) {
        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
        this.validator = validator;
    }

    @GET
    public Response phoneNumberPageGet() throws Exception {

        if (!motrSession.isAllowedOnPhoneNumberEntryPage()) {
            return redirect(HomepageResource.HOMEPAGE_URL);
        }

        if (!motrSession.visitingFromReviewPage()) {
            motrSession.setVisitingFromContactEntry(true);
        }

        Map<String, Object> modelMap = new HashMap<>();
        ReviewFlowUpdater.updateMapBasedOnReviewFlow(
                modelMap,
                motrSession.visitingFromContactEntryPage(),
                motrSession.visitingFromReviewPage()
        );

        String phoneNumber = motrSession.getUnnormalizedPhoneNumberFromSession();
        modelMap.put(PHONE_NUMBER_MODEL_KEY, phoneNumber);

        return Response.ok(renderer.render(PHONE_NUMBER_TEMPLATE, modelMap)).build();
    }

    @POST
    public Response phoneNumberPagePost(@FormParam("phoneNumber") String phoneNumber) throws Exception {

        phoneNumber = PhoneNumberFormatter.trimWhitespace(phoneNumber);

        if (validator.isValid(phoneNumber)) {
            String normalizedUkPhoneNumber = PhoneNumberFormatter.normalizeUkPhoneNumber(phoneNumber);

            motrSession.setChannel("text");
            motrSession.setPhoneNumber(normalizedUkPhoneNumber);
            motrSession.setUnnormalizedPhoneNumber(phoneNumber);

            return redirect("review");
        }

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put(MESSAGE_MODEL_KEY, validator.getMessage());
        modelMap.put(MESSAGE_AT_FIELD_MODEL_KEY, validator.getMessageAtField());
        dataLayerHelper.putAttribute(ERROR_KEY, validator.getMessage());
        ReviewFlowUpdater.updateMapBasedOnReviewFlow(modelMap,
                motrSession.visitingFromContactEntryPage(),
                motrSession.visitingFromReviewPage());

        modelMap.put(PHONE_NUMBER_MODEL_KEY, phoneNumber);
        modelMap.put(INPUT_FIELD_ID_MODEL_KEY, INPUT_FIELD_ID);
        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return Response.ok(renderer.render(PHONE_NUMBER_TEMPLATE, modelMap)).build();
    }
}
