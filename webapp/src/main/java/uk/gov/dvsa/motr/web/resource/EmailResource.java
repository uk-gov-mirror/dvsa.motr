package uk.gov.dvsa.motr.web.resource;

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

import static java.util.Collections.emptyMap;

@Singleton
@Path("/email")
@Produces("text/html")
public class EmailResource {

    private final TemplateEngine renderer;

    @Inject
    public EmailResource(TemplateEngine renderer) {
        this.renderer = renderer;
    }

    @GET
    public String emailPage() throws Exception {

        return renderer.render("email", emptyMap());
    }

    @POST
    public String emailPagePost(@FormParam("emailAddress") String email) throws Exception {

        EmailValidator emailValidator = new EmailValidator();

        if (emailValidator.isValid(email)) {
            //TODO Dynamo DB check and redirection to review page when it's ready
            return renderer.render("email", emptyMap());
        }

        Map<String, String> map = new HashMap<>();
        map.put("message", emailValidator.getMessage());

        return renderer.render("email", map);
    }
}
