package uk.gov.dvsa.motr.web.component.subscription.persistence;

import uk.gov.dvsa.motr.web.component.subscription.model.CancelledSubscription;

public interface CancelledSubscriptionRepository {

    void save(CancelledSubscription cancelledSubscription);
}
