package uk.gov.dvsa.motr.web.remote.client;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VehicleDetailsClientTest {

    private static final String TEST_REG_NUMBER = "some test reg number";
    private static final String TEST_REG_NUMBER_KEY = "vrm";
    private static final String TEST_ENDPOINT_URL = "some/endpoint";

    private static final Client CLIENT_MOCK = mock(Client.class);
    private static final WebTarget WEB_TARGET_MOCK = mock(WebTarget.class);
    private static final Builder BUILDER_MOCK = mock(Builder.class);

    @Test
    public void clientTest() throws Exception {

        when(BUILDER_MOCK.get()).thenReturn(null);
        when(WEB_TARGET_MOCK.request()).thenReturn(BUILDER_MOCK);
        when(WEB_TARGET_MOCK.queryParam(eq(TEST_REG_NUMBER_KEY), eq(TEST_REG_NUMBER))).thenReturn(WEB_TARGET_MOCK);
        when(CLIENT_MOCK.target(any(String.class))).thenReturn(WEB_TARGET_MOCK);

        (new VehicleDetailsClient(WEB_TARGET_MOCK)).retrieveVehicleDetails(TEST_REG_NUMBER);

        verify(WEB_TARGET_MOCK, times(1)).queryParam(TEST_REG_NUMBER_KEY, TEST_REG_NUMBER);
    }
}
