package uk.gov.dvsa.motr.test.integration.subscriptionloader;

import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.subscriptionloader.handler.EventHandler;
import uk.gov.dvsa.motr.subscriptionloader.processing.loader.LoadReport;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixture;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionTable;
import uk.gov.dvsa.motr.test.integration.sqs.SqsHelper;

import java.util.List;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.dynamoDbClient;


public class SubscriptionLoaderTests extends SubscriptionLoaderBase {

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private ObjectMapper jsonMapper = new ObjectMapper();
    private SqsHelper queueHelper;
    private EventHandler eventHandler;
    private SubscriptionItem subscriptionItem;
    private DynamoDbFixture fixture;

    @Before
    public void setUp() {

        queueHelper = new SqsHelper();
        eventHandler = new EventHandler();
        subscriptionItem = new SubscriptionItem().generateMotTestNumber();
        fixture = new DynamoDbFixture(dynamoDbClient());
        fixture.table(new SubscriptionTable().item(subscriptionItem)).run();
    }

    @After
    public void cleanUp() {

        fixture.removeItem(subscriptionItem);
    }

    @Test
    public void runLoaderForOneMonthReminderThenEnsureItemsAddedToQueue() throws Exception {

        String testTime = subscriptionItem.getMotDueDate().minusDays(30) + "T12:00:00Z";
        LoadReport loadReport = eventHandler.handle(buildRequest(testTime), buildContext());

        List<Message> messages = queueHelper.getMessagesFromQueue();

        Subscription subscription = jsonMapper.readValue(messages.get(0).getBody(), Subscription.class);
        queueHelper.deleteMessageFromQueue(messages.get(0));

        assertEquals(subscriptionItem.getMotDueDate(), subscription.getMotDueDate());
        assertEquals(subscriptionItem.getId(), subscription.getId());

        assertSubscriptionIsAddedToQueue(subscription);
        assertReportIsUpdatedCorrectly(loadReport);
    }

    @Test
    public void runLoaderForTwoWeeksReminderThenEnsureItemsAddedToQueue() throws Exception {

        String testTime = subscriptionItem.getMotDueDate().minusDays(14) + "T12:00:00Z";
        LoadReport loadReport = eventHandler.handle(buildRequest(testTime), buildContext());

        List<Message> messages = queueHelper.getMessagesFromQueue();

        Subscription subscription = jsonMapper.readValue(messages.get(0).getBody(), Subscription.class);
        queueHelper.deleteMessageFromQueue(messages.get(0));

        assertSubscriptionIsAddedToQueue(subscription);
        assertReportIsUpdatedCorrectly(loadReport);
    }

    @Test
    public void runLoaderForOneDayAfterReminderThenEnsureItemsAddedToQueue() throws Exception {

        String testTime = subscriptionItem.getMotDueDate().plusDays(1L) + "T12:00:00Z";
        LoadReport loadReport = eventHandler.handle(buildRequest(testTime), buildContext());

        List<Message> messages = queueHelper.getMessagesFromQueue();

        Subscription subscription = jsonMapper.readValue(messages.get(0).getBody(), Subscription.class);
        queueHelper.deleteMessageFromQueue(messages.get(0));

        assertSubscriptionIsAddedToQueue(subscription);
        assertReportIsUpdatedCorrectly(loadReport);
    }

    private void assertSubscriptionIsAddedToQueue(Subscription subscription) {

        assertEquals(subscriptionItem.getVrm(), subscription.getVrm());
        assertEquals(subscriptionItem.getEmail(), subscription.getContactDetail().getValue());
        assertEquals(subscriptionItem.getContactType().getValue(), subscription.getContactDetail().getContactType().getValue());
        assertEquals(subscriptionItem.getMotTestNumber(), subscription.getMotTestNumber());
    }

    private void assertReportIsUpdatedCorrectly(LoadReport loadReport) {

        assertEquals(1, loadReport.getSubmittedForProcessing());
        assertEquals(0, loadReport.getDvlaVehiclesProcessed());
        assertEquals(1, loadReport.getNonDvlaVehiclesProcessed());
        assertEquals(1, loadReport.getTotalProcessed());
    }
}
