package uk.gov.dvsa.motr.notifier.events;

public class OneDayAfterSmsReminderEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "ONE-DAY-AFTER-SMS-SUCCESS";
    }
}
