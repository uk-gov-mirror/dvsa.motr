package uk.gov.dvsa.motr.smsreceiver.events;


import uk.gov.dvsa.motr.eventlog.Event;

public class UserAlreadyUnsubscribedEvent extends Event {

    @Override
    public String getCode() {

        return "USER-ALREADY-UNSUBSCRIBED";
    }

    public UserAlreadyUnsubscribedEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }
}
