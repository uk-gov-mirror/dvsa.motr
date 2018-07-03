package uk.gov.dvs.motr.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.gov.dvsa.motr.client.GoogleAnalyticsClient;
import uk.gov.dvsa.motr.conversion.DataAnonymizer;

import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleAnalyticsClientTest {

    private static final String TRACING_ID = "UA-12345678";
    private static final String ANONYMIZED_CONTACT_DATA = "abcdefghijk";

    private GoogleAnalyticsClient googleAnalyticsClient;
    private DataAnonymizer dataAnonymizer = mock(DataAnonymizer.class);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void setup() {

        when(dataAnonymizer.anonymizeContactData(anyString())).thenReturn(ANONYMIZED_CONTACT_DATA);
        this.googleAnalyticsClient = new GoogleAnalyticsClient(TRACING_ID, dataAnonymizer,
                "http://localhost:8089/collect?v=1&tid=%s&cid=NOT-AVAILABLE");
    }

    @Test
    public void checkSendUnsubscribeEvent() throws URISyntaxException {

        stubFor(post(anyUrl())
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")));

        googleAnalyticsClient.sendUnsubscribeEvent("123456789");

        verify(exactly(1), postRequestedFor(urlEqualTo("/collect?v=1&tid=" + TRACING_ID + "&cid=NOT-AVAILABLE&t=event&ec=MOTR" +
                "&ea=SMS_Confirmed&el=Unsubscribe&cd5=mobile&cd6=unsubscribe&cd7=" + ANONYMIZED_CONTACT_DATA)));
    }
}
