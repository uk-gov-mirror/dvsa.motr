package uk.gov.dvsa.motr.smsreceiver.events;


import uk.gov.dvsa.motr.eventlog.Event;

public class FailedToFindSubscriptionEvent extends Event {

    @Override
    public String getCode() {

        return "SUBSCRIPTION-NOT-FOUND";
    }

    public FailedToFindSubscriptionEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }
}
