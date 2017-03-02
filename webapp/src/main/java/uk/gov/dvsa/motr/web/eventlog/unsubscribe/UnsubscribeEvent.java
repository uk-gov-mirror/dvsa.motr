package uk.gov.dvsa.motr.web.eventlog.unsubscribe;

import uk.gov.dvsa.motr.web.eventlog.subscription.SubscriptionEvent;

public class UnsubscribeEvent extends SubscriptionEvent {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-UNSUBSCRIBE";
    }
}
