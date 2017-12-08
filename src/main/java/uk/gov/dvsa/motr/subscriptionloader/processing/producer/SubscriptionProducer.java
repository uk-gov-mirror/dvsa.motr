package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;

import java.time.LocalDate;
import java.util.Iterator;

public interface SubscriptionProducer {

    Iterator<Subscription> getIterator(LocalDate secondNotificationDate, LocalDate firstNotificationDate);
}
