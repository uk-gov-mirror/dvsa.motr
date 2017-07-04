package uk.gov.dvsa.motr.remote.vehicledetails;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import static java.nio.charset.Charset.defaultCharset;

@RunWith(DataProviderRunner.class)
public class VehicleDetailsClientTest {

    @Rule
    public WireMockRule vehicleDetailsEndpoint = new WireMockRule(8098);

    @Test(expected = VehicleDetailsClientException.class)
    public void throwsClientExceptionWhenInvalidUrl() throws Exception {

        VehicleDetailsClient client = new VehicleDetailsClient(new ClientConfig(), "invalid_url", "api-key");
        client.fetch("11111111111");
    }

    @Test
    public void passesApiKeyAsAHeader() throws Exception {

        stubFor(onRequest().willReturn(aResponse().withBody(validResponse())
                .withHeader("Content-Type", "application/json")));

        withDefaultClient().fetch("11111111111");
    }

    @Test
    public void returnsVehicleDetailsWhenEndpointRespondsWith200() throws Exception {

        stubFor(onRequest().willReturn(aResponse().withBody(validResponse())
                .withHeader("Content-Type", "application/json")));

        VehicleDetails details = withDefaultClient().fetch("11111111111").get();

        assertEquals("MERCEDES-BENZ", details.getMake());
        assertEquals("C220 ELEGANCE ED125 CDI BLU-CY", details.getModel());
        assertEquals("Silver", details.getPrimaryColour());
        assertEquals("", details.getSecondaryColour());
        assertEquals("VRM12345", details.getRegNumber());
        assertEquals(LocalDate.parse("2016-11-26"), details.getMotExpiryDate());
        assertEquals(2006, details.getYearOfManufacture().intValue());
    }

    @Test
    public void returnsEmptyWhenEndpointRespondsWith404() throws Exception {

        stubFor(onRequest().willReturn(aResponse().withStatus(404)));

        Optional<VehicleDetails> response = withDefaultClient().fetch("11111111111");

        assertFalse(response.isPresent());
        verify(getRequestedFor(urlEqualTo("/vehicle-details-endpoint/11111111111")));
    }

    @DataProvider
    public static Object[][] unexpectedEndpointStatusCodes() {

        return new Object[][]{
                {401}, {403}, {500}, {501}, {502}, {503}
        };
    }

    @UseDataProvider("unexpectedEndpointStatusCodes")
    @Test
    public void throwsEndpointExceptionWhenEndpointRespondsWithUnexpectedStatusCode(int statusCode) throws Exception {

        stubFor(onRequest().willReturn(aResponse().withStatus(statusCode).withBody("test.body")));

        try {

            withDefaultClient().fetch("11111111111");
            fail();

        } catch (VehicleDetailsEndpointResponseException ex) {

            assertEquals(statusCode, ex.getStatusCode());
            assertEquals("test.body", ex.getBody());
        }
    }

    @DataProvider
    public static Object[][] badResponses() {

        return new Object[][]{
                new Object[]{Fault.EMPTY_RESPONSE},
                new Object[]{Fault.MALFORMED_RESPONSE_CHUNK},
                new Object[]{Fault.RANDOM_DATA_THEN_CLOSE}
        };
    }

    @UseDataProvider("badResponses")
    @Test(expected = VehicleDetailsClientException.class)
    public void throwsEndpointExceptionWhenEndpointRespondsWithFault(Fault fault) throws Exception {

        stubFor(onRequest().willReturn(aResponse().withFault(fault)));

        withDefaultClient().fetch("11111111111");
    }

    private MappingBuilder onRequest() {

        return get(urlEqualTo("/vehicle-details-endpoint/11111111111"))
                .withHeader("x-api-key", equalTo("api-key"));
    }

    private VehicleDetailsClient withDefaultClient() {

        final String endpointUri = "http://localhost:8098/vehicle-details-endpoint/{number}";
        return new VehicleDetailsClient(new ClientConfig(), endpointUri, "api-key");
    }

    private String validResponse() throws IOException {

        return IOUtils.toString(
                getClass().getClassLoader().getResourceAsStream("vehicledetails/response/ok.json"),
                defaultCharset()
        );
    }
}
