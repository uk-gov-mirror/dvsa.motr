package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;

public class InvalidSubscriptionActivationIdUsedEvent extends Event {

    @Override
    public String getCode() {

        return "INVALID-SUBSCRIPTION-ACTIVATION-ID-USED";
    }
}
