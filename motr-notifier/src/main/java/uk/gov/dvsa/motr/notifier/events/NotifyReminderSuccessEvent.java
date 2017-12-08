package uk.gov.dvsa.motr.notifier.events;

public class NotifyReminderSuccessEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "NOTIFY-REMINDER-SUCCESS";
    }
}
