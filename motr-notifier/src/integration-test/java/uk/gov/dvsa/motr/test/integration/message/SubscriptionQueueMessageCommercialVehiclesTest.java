package uk.gov.dvsa.motr.test.integration.message;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionDbItem;
import uk.gov.dvsa.motr.test.environmant.variables.TestEnvironmentVariables;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model.SubscriptionItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;

@Ignore("These tests rely on the subscription loader running against HGV/PSV vehicles, which is currently disabled (see BL-11356")
public class SubscriptionQueueMessageCommercialVehiclesTest extends SubscriptionQueueMessageAbstractTest {


    @Rule
    public final EnvironmentVariables environmentVariables = new TestEnvironmentVariables();

    private static final LocalDate VEHICLE_TEST_EXPIRY_DATE = LocalDate.of(2026, 3, 9);

    @Test
    public void whenAnHgvSubscriptionIsInTheDb_TheLoaderLoadsIt_TheNotifierProcessIt()
            throws Exception {
        subscriptionItem = new SubscriptionItem()
                .setVehicleType(VehicleType.HGV)
                .setVrm("HGV-OLDEXPIRY")
                .setMotDueDate(VEHICLE_TEST_EXPIRY_DATE);

        SubscriptionDbItem savedItem = saveAndProcessSubscriptionItem(subscriptionItem);
        verifySavedSubscriptionItem(subscriptionItem, savedItem);
    }

    @Test
    public void whenAnPsvSubscriptionIsInTheDb_TheLoaderLoadsIt_TheNotifierProcessIt()
            throws Exception {
        subscriptionItem = new SubscriptionItem()
                .setVehicleType(VehicleType.HGV)
                .setVrm("PSV-ONECOLOR")
                .setMotDueDate(VEHICLE_TEST_EXPIRY_DATE);

        saveAndProcessSubscriptionItem(subscriptionItem);

        SubscriptionDbItem savedItem = saveAndProcessSubscriptionItem(subscriptionItem);
        verifySavedSubscriptionItem(subscriptionItem, savedItem);
    }
}
