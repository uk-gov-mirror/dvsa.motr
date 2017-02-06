package uk.gov.dvsa.motr.web.eventlog.notify;

public class NotifyConfirmationFailureEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "NOTIFY-CONFIRMATION-FAILURE";
    }
}
