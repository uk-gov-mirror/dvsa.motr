package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

public interface SubscriptionProducer {

    Iterator<Subscription> getIterator(List<LocalDate> dates);
}
