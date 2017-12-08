package uk.gov.dvsa.motr.web.eventlog.unsubscribe;

import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionEvent;

public class UnsubscribedEvent extends SubscriptionEvent {

    @Override
    public String getCode() {

        return "UNSUBSCRIBED";
    }
}
