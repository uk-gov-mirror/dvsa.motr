package uk.gov.dvsa.motr.test.integration.subscriptionloader;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.subscriptionloader.processing.loader.LoadReport;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionTable;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class SubscriptionLoaderDvlaVehicleTests extends SubscriptionLoaderBase {

    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private SubscriptionItem subscriptionItem;

    @Before
    public void setUp() {
        super.setUp();
        subscriptionItem = new SubscriptionItem()
            .setVehicleType(VehicleType.MOT);
    }

    @After
    public void cleanUp() {

        fixture.removeItem(subscriptionItem);
    }

    @Test
    public void runLoaderForTwoWeeksReminderWithDvlaIdThenEnsureItemsAddedToQueue() throws Exception {
        subscriptionItem.generateDvlaId();
        fixture.table(new SubscriptionTable().item(subscriptionItem)).run();
        String testTime = subscriptionItem.getMotDueDate().minusDays(14) + "T12:00:00Z";

        LoadReport loadReport = eventHandler.handle(buildRequest(testTime), buildContext());

        Subscription subscription = assertSubscriptionIsAddedToQueue();
        assertNull(subscription.getMotTestNumber());
        assertReportIsUpdatedCorrectly(loadReport, true);

    }

    @Test
    public void runLoaderForTwoWeeksReminderWithMotTestNumberThenEnsureItemsAddedToQueue() throws Exception {
        subscriptionItem.generateMotTestNumber();
        fixture.table(new SubscriptionTable().item(subscriptionItem)).run();
        String testTime = subscriptionItem.getMotDueDate().minusDays(14) + "T12:00:00Z";

        LoadReport loadReport = eventHandler.handle(buildRequest(testTime), buildContext());

        Subscription subscription = assertSubscriptionIsAddedToQueue();
        assertEquals(subscriptionItem.getMotTestNumber(), subscription.getMotTestNumber());
        assertReportIsUpdatedCorrectly(loadReport, false);
    }

    @Test
    public void runLoaderForTwoWeeksReminderWithMotTestNumberAndDvlaIdThenEnsureItemsAddedToQueueWithoutDvlaId() throws Exception {
        subscriptionItem.generateMotTestNumber()
                        .generateDvlaId();
        fixture.table(new SubscriptionTable().item(subscriptionItem)).run();
        String testTime = subscriptionItem.getMotDueDate().minusDays(14) + "T12:00:00Z";

        LoadReport loadReport = eventHandler.handle(buildRequest(testTime), buildContext());

        Subscription subscription = assertSubscriptionIsAddedToQueue();
        assertEquals(subscriptionItem.getMotTestNumber(), subscription.getMotTestNumber());
        assertNull(subscription.getDvlaId());
        assertReportIsUpdatedCorrectly(loadReport, false);
    }

    private Subscription assertSubscriptionIsAddedToQueue() throws IOException {
        Subscription subscription = getQueuedSubscription();

        assertEquals(subscriptionItem.getVrm(), subscription.getVrm());
        assertEquals(subscriptionItem.getEmail(), subscription.getContactDetail().getValue());
        assertEquals(subscriptionItem.getContactType().getValue(), subscription.getContactDetail().getContactType().getValue());
        assertEquals(subscriptionItem.getVehicleType(), subscription.getVehicleType());

        return subscription;
    }

    private void assertReportIsUpdatedCorrectly(LoadReport loadReport, boolean isDvlaVehicle) {
        assertEquals(1, loadReport.getSubmittedForProcessing());
        assertEquals(isDvlaVehicle ? 1 : 0, loadReport.getDvlaVehiclesProcessed());
        assertEquals(isDvlaVehicle ? 0 : 1, loadReport.getNonDvlaVehiclesProcessed());
        assertEquals(1, loadReport.getTotalProcessed());
    }

}
