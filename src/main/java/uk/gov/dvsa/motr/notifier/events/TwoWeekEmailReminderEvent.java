package uk.gov.dvsa.motr.notifier.events;

public class TwoWeekEmailReminderEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "TWO-WEEK-EMAIL-SUCCESS";
    }
}
