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

import uk.gov.dvsa.motr.web.system.binder.factory.VehicleDetailsClientFactory;

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

    private static final String MOT_DUE_DATE_PLACEHOLDER = "MOT-DUE-DATE-PLACEHOLDER";

    private static final String READ_TIMEOUT = "1000";

    @Rule
    public WireMockRule vehicleDetailsEndpoint = new WireMockRule(8098);

    @Test(expected = VehicleDetailsClientException.class)
    public void throwsClientExceptionWhenInvalidUrl() throws Exception {

        VehicleDetailsClient client = new VehicleDetailsClient(new ClientConfig(), "invalid_url", "api-key");
        client.fetch("VRM12345");
    }

    @Test
    public void passesApiKeyAsAHeader() throws Exception {

        stubFor(onRequest().willReturn(aResponse().withBody(validResponse())
                .withHeader("Content-Type", "application/json")));

        withDefaultClient().fetch("VRM12345");
    }

    @Test
    public void returnsVehicleDetailsWhenEndpointRespondsWith200() throws Exception {

        stubFor(onRequest().willReturn(aResponse().withBody(validResponse())
                .withHeader("Content-Type", "application/json")));

        VehicleDetails details = withDefaultClient().fetch("VRM12345").get();

        assertEquals("MERCEDES-BENZ", details.getMake());
        assertEquals("C220 ELEGANCE ED125 CDI BLU-CY", details.getModel());
        assertEquals("Silver", details.getPrimaryColour());
        assertEquals("", details.getSecondaryColour());
        assertEquals("VRM12345", details.getRegNumber());
        assertEquals(LocalDate.parse("2016-11-26"), details.getMotExpiryDate());
        assertEquals(2006, details.getYearOfManufacture().intValue());
    }

    @Test
    @UseDataProvider("dataProviderResponseWithMotDueDate")
    public void vehicleDetailsHandlesUnknownMotDueDate(String vehicleClientResponse) throws Exception {

        stubFor(onRequest().willReturn(aResponse().withBody(vehicleClientResponse)
                .withHeader("Content-Type", "application/json")));

        VehicleDetails details = withDefaultClient().fetch("VRM12345").get();

        assertEquals(null, details.getMotExpiryDate());

        assertEquals("MERCEDES-BENZ", details.getMake());
        assertEquals("C220 ELEGANCE ED125 CDI BLU-CY", details.getModel());
        assertEquals("Silver", details.getPrimaryColour());
        assertEquals("", details.getSecondaryColour());
        assertEquals("VRM12345", details.getRegNumber());
        assertEquals(2006, details.getYearOfManufacture().intValue());
    }

    @Test
    public void returnsEmptyWhenEndpointRespondsWith404() throws Exception {

        stubFor(onRequest().willReturn(aResponse().withStatus(404)));

        Optional<VehicleDetails> response = withDefaultClient().fetch("VRM12345");

        assertFalse(response.isPresent());
        verify(getRequestedFor(urlEqualTo("/vehicle-details-endpoint/VRM12345")));
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

            withDefaultClient().fetch("VRM12345");
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

    @DataProvider
    public static Object[][] dataProviderResponseWithMotDueDate() throws IOException {

        return new Object[][]{
                {validResponseWithMotDueDate("")},
                {validResponseWithMotDueDate("unknown")},
                {validResponseWithMotDueDate("asd ads asd ")},
        };
    }

    @UseDataProvider("badResponses")
    @Test(expected = VehicleDetailsClientException.class)
    public void throwsEndpointExceptionWhenEndpointRespondsWithFault(Fault fault) throws Exception {

        stubFor(onRequest().willReturn(aResponse().withFault(fault)));

        withDefaultClient().fetch("VRM12345");
    }

    @Test(expected = VehicleDetailsClientException.class)
    public void throwsEndpointExceptionWhenEndpointTakesLongerThanReadTimeoutToRespond() throws Exception {

        stubFor(onRequest().willReturn(aResponse().withFixedDelay(Integer.valueOf(READ_TIMEOUT)).withBody(validResponse())));

        withDefaultClient().fetch("VRM12345");
    }

    private MappingBuilder onRequest() {

        return get(urlEqualTo("/vehicle-details-endpoint/VRM12345"))
                .withHeader("x-api-key", equalTo("api-key"));
    }

    private VehicleDetailsClient withDefaultClient() {

        final String endpointUri = "http://localhost:8098/vehicle-details-endpoint/{registration}";
        VehicleDetailsClientFactory clientFactory = new VehicleDetailsClientFactory(endpointUri, "api-key", "1", "10");
        return clientFactory.provide();
    }

    private static String loadResponseMock(String resourcePath) throws IOException {

        return IOUtils.toString(
                VehicleDetailsClientTest.class.getClassLoader().getResourceAsStream(resourcePath),
                defaultCharset()
        );
    }

    private static String validResponse() throws IOException {

        return loadResponseMock("vehicledetails/response/ok.json");
    }

    private static String validResponseWithMotDueDate(String motDueDate) throws IOException {

        return loadResponseMock("vehicledetails/response/ok-mot-due-date-placeholder.json").replace(MOT_DUE_DATE_PLACEHOLDER, motDueDate);
    }

}
