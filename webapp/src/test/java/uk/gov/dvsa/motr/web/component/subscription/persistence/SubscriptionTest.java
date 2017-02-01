package uk.gov.dvsa.motr.web.component.subscription.persistence;

import org.junit.Test;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class SubscriptionTest {

    @Test
    public void subscriptionObjectBuiltCorrectly() {

        String testId = UUID.randomUUID().toString();
        LocalDate testDate = LocalDate.now();
        Subscription subscription = new Subscription(testId);
        subscription.setVrm("VCS1234").setEmail("my@email.com").setMotDueDate(testDate);

        assertEquals(testId, subscription.getId());
        assertEquals("VCS1234", subscription.getVrm());
        assertEquals("my@email.com", subscription.getEmail());
        assertEquals(testDate, subscription.getMotDueDate());
    }
}
