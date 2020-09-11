package uk.gov.dvsa.motr.test.integration.unloader;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@Ignore("These tests rely on the subscription loader running against HGV/PSV vehicles, which is currently disabled (see BL-11356")
public class SubscriptionDbItemQueueItemUnloaderCommercialVehiclesTest extends SubscriptionDbItemQueueItemUnloaderAbstractTest {


    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private static final LocalDate VEHICLE_TEST_EXPIRY_DATE = LocalDate.of(2026, 3, 9);

    @Test
    public void whenAnHgvSubscriptionIsInTheDb_TheLoaderLoadsIt_TheNotifierProcessIt()
            throws IOException, InterruptedException, ExecutionException, NotificationClientException {
        subscriptionItem = new SubscriptionItem()
                .setVehicleType(VehicleType.HGV)
                .setVrm("HGV-OLDEXPIRY")
                .setMotDueDate(VEHICLE_TEST_EXPIRY_DATE);

        saveAndProcessSubscriptionItem(subscriptionItem);
    }

    @Test
    public void whenAnPsvSubscriptionIsInTheDb_TheLoaderLoadsIt_TheNotifierProcessIt()
            throws IOException, InterruptedException, ExecutionException, NotificationClientException {
        subscriptionItem = new SubscriptionItem()
                .setVehicleType(VehicleType.HGV)
                .setVrm("PSV-ONECOLOR")
                .setMotDueDate(VEHICLE_TEST_EXPIRY_DATE);

        saveAndProcessSubscriptionItem(subscriptionItem);
    }
}
