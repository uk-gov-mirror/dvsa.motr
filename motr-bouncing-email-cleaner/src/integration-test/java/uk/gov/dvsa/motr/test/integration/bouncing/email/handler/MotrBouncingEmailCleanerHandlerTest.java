package uk.gov.dvsa.motr.test.integration.bouncing.email.handler;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.encryption.AwsKmsDecryptor;
import uk.gov.dvsa.motr.encryption.Decryptor;
import uk.gov.dvsa.motr.handler.MotrBouncingEmailCleanerHandler;
import uk.gov.dvsa.motr.report.BouncingEmailCleanerReport;
import uk.gov.dvsa.motr.service.EmailMessageStatusService;
import uk.gov.dvsa.motr.service.NotifyService;
import uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.cancelledSubscriptionTableName;
import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.govNotifyApiToken;
import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.govNotifyTemplateId;
import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.region;
import static uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables.subscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.dynamoDbClient;

public class MotrBouncingEmailCleanerHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(MotrBouncingEmailCleanerHandlerTest.class);

    private MotrBouncingEmailCleanerHandler handler;
    private NotifyService notifyService;
    private EmailMessageStatusService emailMessageStatusService;

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    @Before
    public void setUp() {

        handler = new MotrBouncingEmailCleanerHandler();
        Decryptor decryptor = new AwsKmsDecryptor(Region.getRegion(Regions.fromName(region())));
        notifyService = new NotifyService(new NotificationClient(decryptor.decrypt(govNotifyApiToken())));
        emailMessageStatusService = new EmailMessageStatusService(new NotificationClient(decryptor.decrypt(govNotifyApiToken())));
    }

    @Test
    public void whenThereIsAnEmailWithAPermFailureStatus_thenItWillBeProcessedAndCancelled() throws NotificationClientException {

        SubscriptionItem subscriptionItem = new SubscriptionItem();
        subscriptionItem.setEmail("perm-fail@simulator.notify");

        if (doesAnEmailNeedSent()) {
            logger.info("Sending an email as none has been sent");
            notifyService.sendEmail(subscriptionItem.getEmail(), subscriptionItem.getVrm(), subscriptionItem.getMotDueDate(),
                    govNotifyTemplateId());
        }

        new DynamoDB(dynamoDbClient()).getTable(subscriptionTableName()).putItem(subscriptionItem.toItem());
        BouncingEmailCleanerReport amount = handler.handleRequest(new Object(), buildContext());
        // assert not null as the same gov notify token is used across environments and we cannot purge their queue
        assertNotNull(amount.getNumberOfSubscriptionsSuccessfullyCancelled());

        Item cancelledItem = new DynamoDB(dynamoDbClient()).getTable(cancelledSubscriptionTableName())
                .getItem(new GetItemSpec().withPrimaryKey("email", subscriptionItem.getEmail(), "id", subscriptionItem.getId()));

        assertEquals(cancelledItem.getString("id"), subscriptionItem.getId());
        assertEquals(cancelledItem.getString("vrm"), subscriptionItem.getVrm());
        assertEquals(cancelledItem.getString("email"), subscriptionItem.getEmail());
        assertEquals(cancelledItem.getString("reason_for_cancellation"), "Permanently failing");
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

    private boolean doesAnEmailNeedSent() throws NotificationClientException {
        return !(emailMessageStatusService.getEmailAddressesAssociatedWithPermanentlyFailingNotifications().size() >= 1);
    }
}
