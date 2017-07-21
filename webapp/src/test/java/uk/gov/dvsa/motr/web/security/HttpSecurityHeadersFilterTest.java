package uk.gov.dvsa.motr.web.security;

import org.junit.Test;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpSecurityHeadersFilterTest {

    @Test
    public void filterInjectsExpectedHttpResponseHeaderValues() throws Exception {

        ContainerResponseContext responseContext = responseContext();

        new HttpSecurityHeadersFilter().filter(null, responseContext);

        MultivaluedMap<String, Object> responseHeaders = responseContext.getHeaders();

        assertHeaderExistsWithExpectedValue(responseHeaders, "X-Frame-Options", "DENY");
        assertHeaderExistsWithExpectedValue(responseHeaders, "X-XSS-Protection", "1");
        assertHeaderExistsWithExpectedValue(responseHeaders, "Strict-Transport-Security",
                "max-age=15768000; includeSubDomains; preload");
        assertHeaderExistsWithExpectedValue(responseHeaders, "X-Content-Type-Options", "nosniff");
    }

    private void assertHeaderExistsWithExpectedValue(MultivaluedMap<String, Object> responseHeaders, String headerName, String
            headerValue) {

        assertNotNull("Header not present: " + headerName, responseHeaders.containsKey(headerName));
        assertTrue("Must be one header value present: " + headerName, responseHeaders.get(headerName).size() == 1);
        assertEquals("Header value incorrect: " + headerValue, responseHeaders.get(headerName).get(0).toString(), headerValue);
    }

    private static ContainerResponseContext responseContext() {

        final MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();

        ContainerResponseContext context = mock(ContainerResponseContext.class);
        when(context.getHeaders()).thenReturn(map);

        return context;
    }
}
