package uk.gov.dvsa.motr.notifier.events;

public class OneMonthEmailReminderEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "ONE-MONTH-EMAIL-SUCCESS";
    }
}
