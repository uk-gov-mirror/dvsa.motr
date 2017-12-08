package uk.gov.dvsa.motr.subscriptionloader.event;

import uk.gov.dvsa.motr.eventlog.Event;

public class LoadingSuccess extends Event {

    @Override
    public String getCode() {

        return "LOADING_SUCCESS";
    }

    public LoadingSuccess setProcessed(int processed) {

        params.put("processed", String.valueOf(processed));
        return this;
    }

    public LoadingSuccess setDuration(long duration) {

        params.put("duration-ms", String.valueOf(duration));
        return this;
    }
}
