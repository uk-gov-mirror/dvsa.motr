package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class UnloadingTimedOutEvent extends Event {

    @Override
    public String getCode() {

        return "UNLOADING-TIMEDOUT";
    }

    public UnloadingTimedOutEvent setProcessed(int processed) {

        params.put("processed", String.valueOf(processed));
        return this;
    }

    public UnloadingTimedOutEvent setDuration(long duration) {

        params.put("duration-ms", String.valueOf(duration));
        return this;
    }
}
