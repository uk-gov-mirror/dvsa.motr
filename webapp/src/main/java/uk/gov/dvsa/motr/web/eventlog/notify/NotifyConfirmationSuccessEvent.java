package uk.gov.dvsa.motr.web.eventlog.notify;

public class NotifyConfirmationSuccessEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "NOTIFY-CONFIRMATION-SUCCESS";
    }
}
