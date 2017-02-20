package uk.gov.dvsa.motr.subscriptionloader.event;

import uk.gov.dvsa.motr.eventlog.Event;

public class LoadingError extends Event {

    @Override
    public String getCode() {

        return "LOADING_ERROR";
    }
}
