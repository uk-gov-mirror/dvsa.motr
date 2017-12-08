package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.EmailValidator;

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
@Path("/email")
@Produces("text/html")
public class EmailResource {

    private static final String EMAIL_MODEL_KEY = "email";
    private static final String EMAIL_TEMPLATE_NAME = "email";
    private static final String MESSAGE_MODEL_KEY = "message";
    private static final String INPUT_FIELD_ID = "email-input";
    private static final String INPUT_FIELD_ID_MODEL_KEY = "inputFieldId";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;

    private DataLayerHelper dataLayerHelper;

    @Inject
    public EmailResource(
            MotrSession motrSession,
            TemplateEngine renderer
    ) {

        this.motrSession = motrSession;
        this.renderer = renderer;
        this.dataLayerHelper = new DataLayerHelper();
    }

    @GET
    public Response emailPage() throws Exception {

        if (!this.motrSession.isAllowedOnEmailPage()) {
            return redirect(HomepageResource.HOMEPAGE_URL);
        }

        if (!motrSession.visitingFromReviewPage()) {
            motrSession.setVisitingFromContactEntry(true);
        }

        String email = this.motrSession.getEmailFromSession();

        Map<String, Object> modelMap = new HashMap<>();
        ReviewFlowUpdater.updateMapBasedOnReviewFlow(modelMap,
                motrSession.visitingFromContactEntryPage(),
                motrSession.visitingFromReviewPage());

        modelMap.put(EMAIL_MODEL_KEY, email);

        return Response.ok(renderer.render(EMAIL_TEMPLATE_NAME, modelMap)).build();
    }

    @POST
    public Response emailPagePost(@FormParam("emailAddress") String email) throws Exception {

        EmailValidator emailValidator = new EmailValidator();

        if (!motrSession.visitingFromReviewPage()) {
            motrSession.setVisitingFromContactEntry(true);
        }

        if (emailValidator.isValid(email)) {
            this.motrSession.setChannel("email");
            this.motrSession.setEmail(email);

            //TODO Dynamo DB check and redirection to review page when it's ready
            return redirect("review");
        }

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put(MESSAGE_MODEL_KEY, emailValidator.getMessage());
        dataLayerHelper.putAttribute(ERROR_KEY, emailValidator.getMessage());
        ReviewFlowUpdater.updateMapBasedOnReviewFlow(modelMap,
                motrSession.visitingFromContactEntryPage(),
                motrSession.visitingFromReviewPage());

        modelMap.put(EMAIL_MODEL_KEY, email);
        modelMap.put(INPUT_FIELD_ID_MODEL_KEY, INPUT_FIELD_ID);
        modelMap.putAll(dataLayerHelper.formatAttributes());
        dataLayerHelper.clear();

        return Response.ok(renderer.render(EMAIL_TEMPLATE_NAME, modelMap)).build();
    }
}
