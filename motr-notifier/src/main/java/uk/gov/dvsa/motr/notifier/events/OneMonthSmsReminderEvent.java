package uk.gov.dvsa.motr.notifier.events;

public class OneMonthSmsReminderEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "ONE-MONTH-SMS-SUCCESS";
    }
}
