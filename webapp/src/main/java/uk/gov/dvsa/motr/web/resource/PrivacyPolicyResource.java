package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/privacy-policy")
@Produces("text/html")
public class PrivacyPolicyResource {

    private static final String PRIVACY_POLICY_TEMPLATE_NAME = "privacy-policy";

    private final TemplateEngine renderer;

    @Inject
    public PrivacyPolicyResource(TemplateEngine renderer) {

        this.renderer = renderer;
    }

    @GET
    public String privacyPage() {

        return renderer.render(PRIVACY_POLICY_TEMPLATE_NAME, emptyMap());
    }
}

