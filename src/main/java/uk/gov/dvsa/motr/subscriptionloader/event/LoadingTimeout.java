package uk.gov.dvsa.motr.subscriptionloader.event;

import uk.gov.dvsa.motr.eventlog.Event;

public class LoadingTimeout extends Event {


    @Override
    public String getCode() {

        return "LOADING_TIMEOUT";
    }

    public LoadingTimeout setSubmittedForProcessing(int submittedForProcessing) {

        params.put("submitted-for-processing", String.valueOf(submittedForProcessing));
        return this;
    }

    public LoadingTimeout setProcessed(int processed) {

        params.put("processed", String.valueOf(processed));
        return this;
    }

    public LoadingTimeout setDuration(long duration) {

        params.put("duration", String.valueOf(duration));
        return this;
    }
}
