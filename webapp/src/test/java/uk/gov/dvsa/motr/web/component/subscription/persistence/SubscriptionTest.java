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
        String motTestNumber = "123456";
        Subscription subscription = new Subscription();
        subscription.setUnsubscribeId(testId).setVrm("VCS1234").setEmail("my@email.com").setMotDueDate(testDate);
        subscription.setMotTestNumber(motTestNumber);

        assertEquals(testId, subscription.getUnsubscribeId());
        assertEquals("VCS1234", subscription.getVrm());
        assertEquals("my@email.com", subscription.getEmail());
        assertEquals(testDate, subscription.getMotDueDate());
        assertEquals(motTestNumber, subscription.getMotTestNumber());
    }
}
