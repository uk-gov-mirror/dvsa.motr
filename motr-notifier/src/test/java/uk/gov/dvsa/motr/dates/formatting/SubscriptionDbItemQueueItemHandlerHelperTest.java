package uk.gov.dvsa.motr.dates.formatting;

import org.junit.Test;

import java.time.LocalDate;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motDueDateUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.oneMonthEmailRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.twoWeekEmailRequired;

public class SubscriptionDbItemQueueItemHandlerHelperTest {

    @Test
    public void subscriptionUpdateRequiredWhenDatesNotTheSame() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2018, 10, 10);

        assertTrue(motDueDateUpdateRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void subscriptionUpdateNotRequiredWhenDatesAreTheSame() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 10, 10);

        assertFalse(motDueDateUpdateRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneMonthSubscriptionShouldBeSentIfDatesAreOneMonthApart() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 11, 10);

        assertTrue(oneMonthEmailRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void oneMonthSubscriptionShouldNotBeSentIfDatesAreNotOneMonthApart() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 10, 11);

        assertFalse(oneMonthEmailRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void twoWeekSubscriptionShouldBeSentIfDatesAreTwoWeeksApart() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 10, 24);

        assertTrue(twoWeekEmailRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }

    @Test
    public void twoWeekSubscriptionShouldNotBeSentIfDatesAreNotTwoWeeksApart() {
        LocalDate subscriptionMotDueDate = LocalDate.of(2017, 10, 10);
        LocalDate vehicleDetailsMotExpiryDate = LocalDate.of(2017, 10, 11);

        assertFalse(twoWeekEmailRequired(subscriptionMotDueDate, vehicleDetailsMotExpiryDate));
    }
}
