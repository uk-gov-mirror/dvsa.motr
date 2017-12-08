package uk.gov.dvsa.motr.web.system;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class InjectingBaseUrlRedirectFilterTest {

    @DataProvider
    public static Object[][] baseUrl() {
        // @formatter:off
        return new Object[][]{
                {"https://baseUrl/", "/vrm", "https://baseUrl/vrm"},
                {"https://baseUrl", "/vrm", "https://baseUrl/vrm"},
                {"/", "//vrm", "/vrm"},
                {"/", "vrm", "/vrm"},
        };
    }
    // @formatter:on

    @UseDataProvider("baseUrl")
    @Test
    public void filterInjectsBaseUrlCorrectlyWhenLocationHeaderExistsAndPathIsNotAbsolute(
            String baseUrl,
            String redirectUrl,
            String resultUrl
    ) throws Exception {

        ContainerResponseContext responseContext = responseContext(redirectUrl);

        new InjectingBaseUrlRedirectFilter(baseUrl).filter(null, responseContext);

        assertEquals(responseContext.getHeaders().get("Location").get(0).toString(), resultUrl);
    }

    private static ContainerResponseContext responseContext(String redirectUrl) {

        final URI uri;
        try {

            uri = new URI(redirectUrl);

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        final MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.add("Location", uri);

        ContainerResponseContext context = mock(ContainerResponseContext.class);
        when(context.getHeaders()).thenReturn(map);

        return context;
    }
}
