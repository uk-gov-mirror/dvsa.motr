package uk.gov.dvsa.motr.smsreceiver.events;


import uk.gov.dvsa.motr.eventlog.Event;

public class SmsUnsubscribedConfirmationEvent extends Event {

    @Override
    public String getCode() {

        return "SMS-UNSUBSCRIBED-CONFIRMATION";
    }

    public SmsUnsubscribedConfirmationEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }
}
