package uk.gov.dvsa.motr.notifier.events;

public class NotifyReminderFailedEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "NOTIFY-REMINDER-FAILED";
    }
}
