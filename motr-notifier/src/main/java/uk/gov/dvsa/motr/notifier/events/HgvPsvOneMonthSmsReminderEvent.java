package uk.gov.dvsa.motr.notifier.events;

public class HgvPsvOneMonthSmsReminderEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "HGV-PSV-ONE-MONTH-SMS-SUCCESS";
    }
}
