package uk.gov.dvsa.motr.web.system;

import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import static uk.gov.dvsa.motr.web.system.SystemVariable.BASE_URL;

import static java.util.Collections.singletonList;

@Provider
public class InjectingBaseUrlRedirectFilter implements ContainerResponseFilter {

    private static final String HEADER_LOCATION = "Location";
    private String baseUrl;

    public InjectingBaseUrlRedirectFilter(@SystemVariableParam(BASE_URL) String baseUrl) {

        this.baseUrl = baseUrl;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        URI locationUri = (URI) responseContext.getHeaders().getFirst(HEADER_LOCATION);

        boolean isRedirectRelative = locationUri != null && !locationUri.isAbsolute();
        if (isRedirectRelative) {

            try {
                URI uri = new URI(normalizeSlashes(baseUrl + locationUri.toString()));
                responseContext.getHeaders().put(HEADER_LOCATION, singletonList(uri));
            } catch (Exception uriException) {
                throw new RuntimeException(uriException);
            }
        }
    }

    private static String normalizeSlashes(String input) {

        return input.replaceAll("(?<!(http:|https:))[//]+", "/");
    }
}
