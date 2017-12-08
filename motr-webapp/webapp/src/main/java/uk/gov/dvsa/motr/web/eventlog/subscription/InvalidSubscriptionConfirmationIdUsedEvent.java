package uk.gov.dvsa.motr.web.eventlog.subscription;

import uk.gov.dvsa.motr.eventlog.Event;

public class InvalidSubscriptionConfirmationIdUsedEvent extends Event {

    @Override
    public String getCode() {

        return "INVALID-SUBSCRIPTION-CONFIRMATION-ID-USED";
    }

    public InvalidSubscriptionConfirmationIdUsedEvent setUsedId(String usedId) {

        params.put("used-id", usedId);
        return this;
    }
}
