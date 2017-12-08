package uk.gov.dvsa.motr.notifier.events;

public class OneDayAfterEmailReminderEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "ONE-DAY-AFTER-EMAIL-SUCCESS";
    }
}
