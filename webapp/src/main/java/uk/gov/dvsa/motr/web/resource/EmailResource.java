package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.validator.EmailValidator;

import java.net.URI;
import java.net.URISyntaxException;
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
import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;

@Singleton
@Path("/email")
@Produces("text/html")
public class EmailResource {

    private static final String EMAIL_MODEL_KEY = "email";
    private static final String EMAIL_TEMPLATE_NAME = "email";
    private static final String MESSAGE_MODEL_KEY = "message";

    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private final String baseUrl;

    @Inject
    public EmailResource(
            @SystemVariableParam(BASE_URL) String baseUrl,
            MotrSession motrSession,
            TemplateEngine renderer
    ) {

        this.baseUrl = baseUrl;
        this.motrSession = motrSession;
        this.renderer = renderer;
    }

    @GET
    public Response emailPage() throws Exception {

        if (!this.motrSession.isAllowedOnEmailPage()) {
            return Response.status(Response.Status.FOUND).location(getFullUriForPage("/")).build();
        }

        String email = this.motrSession.getEmailFromSession();

        Map<String, Object> modelMap = new HashMap<>();
        updateMapBasedOnReviewFlow(modelMap);

        modelMap.put(EMAIL_MODEL_KEY, email);

        return Response.ok().entity(renderer.render(EMAIL_TEMPLATE_NAME, modelMap)).build();
    }

    @POST
    public Response emailPagePost(@FormParam("emailAddress") String email) throws Exception {

        EmailValidator emailValidator = new EmailValidator();

        if (emailValidator.isValid(email)) {
            this.motrSession.setEmail(email);

            //TODO Dynamo DB check and redirection to review page when it's ready
            return Response.seeOther(getFullUriForPage("review")).build();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(MESSAGE_MODEL_KEY, emailValidator.getMessage());
        updateMapBasedOnReviewFlow(map);

        map.put(EMAIL_MODEL_KEY, email);

        return Response.status(200).entity(renderer.render(EMAIL_TEMPLATE_NAME, map)).build();
    }

    private URI getFullUriForPage(String page) throws URISyntaxException {

        return UriBuilder.fromUri(new URI(this.baseUrl)).path(page).build();
    }

    private void updateMapBasedOnReviewFlow(Map<String, Object> modelMap) throws URISyntaxException {

        if (this.motrSession.visitingFromReviewPage()) {
            modelMap.put("continue_button_text", "Save and return to review");
            modelMap.put("back_button_text", "Cancel and return");
            modelMap.put("back_url", getFullUriForPage("review"));
        } else {
            modelMap.put("continue_button_text", "Continue");
            modelMap.put("back_button_text", "Back");
            modelMap.put("back_url", getFullUriForPage("vrm"));
        }
    }
}
