package uk.gov.dvsa.motr.smsreceiver.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class InvalidVrmSentEvent extends Event {

    @Override
    public String getCode() {

        return "INVALID-VRM-SENT";
    }

    public InvalidVrmSentEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }

    public InvalidVrmSentEvent setMessage(String validationMessage) {

        params.put("validation-message", validationMessage);
        return this;
    }
}
