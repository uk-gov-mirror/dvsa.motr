package uk.gov.dvsa.motr.web.resource;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.system.SystemVariable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriBuilder;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/")
@Produces("text/html")
public class HomepageResource {

    private static final Logger logger = LoggerFactory.getLogger(HomepageResource.class);

    private final TemplateEngine renderer;
    private final MotrSession motrSession;
    private final String baseUrl;

    @Inject
    public HomepageResource(
            @SystemVariableParam(SystemVariable.BASE_URL) String baseUrl,
            MotrSession motrSession,
            TemplateEngine renderer
    ) {

        this.baseUrl = baseUrl;
        this.motrSession = motrSession;
        this.renderer = renderer;
    }

    @GET
    public String homePage() throws Exception {
        
        this.motrSession.setShouldClearCookies(true);
        Map<String, Object> map = new HashMap<>();
        map.put("vrm_url", getFullUriForPage("vrm"));
        return renderer.render("home", map);
    }

    private URI getFullUriForPage(String page) throws URISyntaxException {

        return UriBuilder.fromUri(new URI(this.baseUrl)).path(page).build();
    }
}
