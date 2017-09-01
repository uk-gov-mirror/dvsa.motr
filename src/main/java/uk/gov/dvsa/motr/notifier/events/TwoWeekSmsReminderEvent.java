package uk.gov.dvsa.motr.notifier.events;

public class TwoWeekSmsReminderEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "TWO-WEEK-SMS-SUCCESS";
    }
}
