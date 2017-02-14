package uk.gov.dvsa.motr.web.eventlog.notify;

import uk.gov.dvsa.motr.eventlog.Event;

public abstract class NotifyEvent extends Event {

    public NotifyEvent setEmail(String email) {

        params.put("email", email);
        return this;
    }

    public NotifyEvent setVrm(String vrm) {

        params.put("vrm", vrm);
        return this;
    }
}
