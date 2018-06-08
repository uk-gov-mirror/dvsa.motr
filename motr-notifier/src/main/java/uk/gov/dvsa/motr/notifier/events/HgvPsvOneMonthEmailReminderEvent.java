package uk.gov.dvsa.motr.notifier.events;

public class HgvPsvOneMonthEmailReminderEvent extends NotifyEvent {

    @Override
    public String getCode() {

        return "HGV-PSV-ONE-MONTH-EMAIL-SUCCESS";
    }
}
