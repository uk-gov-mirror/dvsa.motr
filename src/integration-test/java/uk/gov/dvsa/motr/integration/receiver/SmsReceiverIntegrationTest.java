package uk.gov.dvsa.motr.integration.receiver;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.smsreceiver.handler.EventHandler;
import uk.gov.dvsa.motr.smsreceiver.subscription.model.Subscription;
import uk.gov.dvsa.motr.smsreceiver.subscription.persistence.DynamoDbCancelledSubscriptionRepository;
import uk.gov.dvsa.motr.smsreceiver.subscription.persistence.DynamoDbSubscriptionRepository;
import uk.gov.dvsa.motr.test.data.TestMessage;
import uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables;
import uk.gov.dvsa.motr.test.integration.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.fixture.model.SubscriptionTable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables.cancelledSubscriptionTableName;
import static uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables.region;
import static uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables.subscriptionTableName;
import static uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables.token;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.dynamoDbClient;

@SuppressWarnings("unchecked")
public class SmsReceiverIntegrationTest {

    private static final String AUTHORISATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN = "Bearer ";

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private SubscriptionItem subscriptionItem;
    private DynamoDbFixture fixture;
    private EventHandler eventHandler;
    private DynamoDbSubscriptionRepository subscriptionRepo;
    private DynamoDbCancelledSubscriptionRepository cancelledSubscriptionRepo;

    @Before
    public void setUp() {

        subscriptionRepo = new DynamoDbSubscriptionRepository(subscriptionTableName(), region());
        cancelledSubscriptionRepo = new DynamoDbCancelledSubscriptionRepository(cancelledSubscriptionTableName(), region());
        fixture = new DynamoDbFixture(dynamoDbClient());
        eventHandler = new EventHandler();
    }

    @After
    public void cleanUp() {

        fixture.removeItem(subscriptionItem);
    }

    @Test
    public void whenCallingTheReceiverWithAValidSmsMessage_ThenTheSubscriptionIsCancelled() throws Exception {

        subscriptionItem = new SubscriptionItem();
        fixture.table(new SubscriptionTable().item(subscriptionItem)).run();

        Optional<Subscription> sub = subscriptionRepo
                .findByVrmAndMobileNumber(subscriptionItem.getVrm(), subscriptionItem.getMobileNumber());
        assertTrue(sub.isPresent());


        eventHandler.handle(buildReceiverRequest(true), buildContext());

        sub = subscriptionRepo.findByVrmAndMobileNumber(subscriptionItem.getVrm(), subscriptionItem.getMobileNumber());

        assertFalse(sub.isPresent());
        assertTrue(foundMatchingCancelledSubscription(subscriptionItem.getVrm(), subscriptionItem.getMobileNumber()));
    }

    @Test
    public void whenCallingTheReceiverWithAAnInValidSmsMessage_ThenTheSubscriptionIsNotCancelled() throws Exception {

        subscriptionItem = new SubscriptionItem();
        fixture.table(new SubscriptionTable().item(subscriptionItem)).run();

        Optional<Subscription> sub = subscriptionRepo
                .findByVrmAndMobileNumber(subscriptionItem.getVrm(), subscriptionItem.getMobileNumber());
        assertTrue(sub.isPresent());

        eventHandler.handle(buildReceiverRequest(false), buildContext());

        sub = subscriptionRepo.findByVrmAndMobileNumber(subscriptionItem.getVrm(), subscriptionItem.getMobileNumber());

        assertTrue(sub.isPresent());
        assertFalse(foundMatchingCancelledSubscription(subscriptionItem.getVrm(), subscriptionItem.getMobileNumber()));
    }

    private boolean foundMatchingCancelledSubscription(String vrm, String mobileNumber) {

        Iterator<Item> items = cancelledSubscriptionRepo.findCancelledSubscriptionByVrmAndMobile(vrm, mobileNumber);
        while (items.hasNext()) {
            Item item = items.next();
            if (vrm.equalsIgnoreCase(item.getString("vrm")) && mobileNumber.equals(item.getString("email"))) {
                return true;
            }
        }
        return false;
    }

    private AwsProxyRequest buildReceiverRequest(boolean includeVrm) throws JsonProcessingException {

        TestMessage testMessage = new TestMessage();
        testMessage.setSourceNumber(subscriptionItem.getMobileNumber());

        if (includeVrm) {
            String mixedCaseVrm = mixTheCase(subscriptionItem.getVrm());
            testMessage.setMessage("STOP " + mixedCaseVrm);
        } else {
            testMessage.setMessage("STOP ");
        }

        ObjectMapper mapper = new ObjectMapper();
        String testMessageAsJson = mapper.writeValueAsString(testMessage);

        AwsProxyRequest awsProxyRequest = new AwsProxyRequest();
        awsProxyRequest.setBody(testMessageAsJson);

        Map<String, String> headers = new HashMap();
        headers.put(AUTHORISATION_HEADER, BEARER_TOKEN + token());
        awsProxyRequest.setHeaders(headers);

        return awsProxyRequest;
    }

    private Context buildContext() {

        return new Context() {
            @Override
            public String getAwsRequestId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public String getLogGroupName() {
                return null;
            }

            @Override
            public String getLogStreamName() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }

            @Override
            public String getFunctionVersion() {
                return null;
            }

            @Override
            public String getInvokedFunctionArn() {
                return null;
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {

                return 400000;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return null;
            }
        };
    }

    private String mixTheCase(String stringToMixUp) {

        String newString = "";
        for (int i = 0; i < stringToMixUp.length() ; i++) {
            char charAtIndex = stringToMixUp.charAt(i);
            newString += (Math.random() <= 0.5) ? Character.toUpperCase(charAtIndex) : Character.toLowerCase(charAtIndex);
        }
        return newString;
    }
}
