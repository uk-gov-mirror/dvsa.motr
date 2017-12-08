package uk.gov.dvsa.motr.subscriptionloader.event;

import uk.gov.dvsa.motr.eventlog.Event;

public class LoadingError extends Event {

    @Override
    public String getCode() {

        return "LOADING-ERROR";
    }

    public LoadingError setProcessed(int processed) {

        params.put("processed", String.valueOf(processed));
        return this;
    }
}
