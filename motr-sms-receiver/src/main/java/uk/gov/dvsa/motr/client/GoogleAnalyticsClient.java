package uk.gov.dvsa.motr.client;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;

import uk.gov.dvsa.motr.conversion.DataAnonymizer;
import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.helper.SystemVariableParam;
import uk.gov.dvsa.motr.smsreceiver.events.FailedToSendGaUnsubscribeRequestEvent;
import uk.gov.dvsa.motr.smsreceiver.events.GaUnsubscribeRequestSentEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static uk.gov.dvsa.motr.smsreceiver.system.SystemVariable.GA_TRACING_ID;

public class GoogleAnalyticsClient {

    private static final int COMPLETABLE_FUTURE_TIMEOUT_SECONDS = 10;

    private static final String TYPE_PARAM = "t";
    private static final String EVENT_CATEGORY_PARAM = "ec";
    private static final String EVENT_ACTION_PARAM = "ea";
    private static final String EVENT_LABEL_PARAM = "el";
    private static final String CONTACT_TYPE_PARAM = "cd5";
    private static final String EVENT_TYPE_PARAM = "cd6";
    private static final String CONTACT_ID_PARAM = "cd7";

    private Client client;
    private DataAnonymizer dataAnonymizer;
    private String tracingId;
    private String googleAnalyticsUrl;

    public GoogleAnalyticsClient(@SystemVariableParam(GA_TRACING_ID) String tracingId,
                                 DataAnonymizer dataAnonymizer,
                                 String googleAnalyticsUrl) {

        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        this.client = JerseyClientBuilder.newClient(clientConfig);
        this.tracingId = tracingId;
        this.dataAnonymizer = dataAnonymizer;
        this.googleAnalyticsUrl = googleAnalyticsUrl;
    }

    public void sendUnsubscribeEvent(String phoneNumber) {

        String anonymizedPhoneNumber = dataAnonymizer.anonymizeContactData(phoneNumber);

        try {
            URI uri = buildUri(tracingId, buildPayloadParamsForUnsubscribeEvent(anonymizedPhoneNumber));
            CompletableFuture.supplyAsync(() -> doRequest(uri)).get(COMPLETABLE_FUTURE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            EventLogger.logEvent(new GaUnsubscribeRequestSentEvent());
        } catch (Exception e) {
            EventLogger.logErrorEvent(new FailedToSendGaUnsubscribeRequestEvent(), e);
        }
    }

    private List<String> buildPayloadParamsForUnsubscribeEvent(String contactIdParam) {

        List<String> payloadParams = new ArrayList<>();
        payloadParams.add(createPayloadParam(TYPE_PARAM, "event"));
        payloadParams.add(createPayloadParam(EVENT_CATEGORY_PARAM, "MOTR"));
        payloadParams.add(createPayloadParam(EVENT_ACTION_PARAM, "SMS_Confirmed"));
        payloadParams.add(createPayloadParam(EVENT_LABEL_PARAM, "Unsubscribe"));
        payloadParams.add(createPayloadParam(CONTACT_TYPE_PARAM, "mobile"));
        payloadParams.add(createPayloadParam(EVENT_TYPE_PARAM, "unsubscribe"));
        payloadParams.add(createPayloadParam(CONTACT_ID_PARAM, contactIdParam));

        return payloadParams;
    }

    private String createPayloadParam(String name, String value) {

        return String.format("&%s=%s", name, value);
    }

    private URI buildUri(String tracingId, List<String> payloadParams) throws URISyntaxException {

        String path = String.format(googleAnalyticsUrl, tracingId)
                .concat(payloadParams.stream().collect(Collectors.joining()));

        return new URI(path);
    }

    private Response doRequest(URI uri) {

        try {
            return client.target(uri)
                    .request()
                    .header("Content-Length", "0")
                    .post(null);
        } catch (Exception e) {
            EventLogger.logErrorEvent(new FailedToSendGaUnsubscribeRequestEvent(), e);
            return null;
        }
    }
}
