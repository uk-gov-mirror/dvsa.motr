package uk.gov.dvsa.motr.web.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

class RedirectResponseBuilder {

    static Response redirect(String uri) {

        try {
            return Response.status(302).location(new URI(uri)).build();
        } catch (URISyntaxException syntaxException) {
            throw new RuntimeException(syntaxException);
        }
    }
}
