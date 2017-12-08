package uk.gov.dvsa.motr.event;

import uk.gov.dvsa.motr.eventlog.Event;

public class NotificationClientErrorEvent extends Event {

    @Override
    public String getCode() {

        return "NOTIFICATION-CLIENT-ERROR";
    }
}
