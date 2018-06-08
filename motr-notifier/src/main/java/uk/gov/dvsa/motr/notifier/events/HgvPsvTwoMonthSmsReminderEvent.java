package uk.gov.dvsa.motr.notifier.events;

public class HgvPsvTwoMonthSmsReminderEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "TWO-MONTH-HGV-PSV-SMS-SUCCESS";
    }
}
