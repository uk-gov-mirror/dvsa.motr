package uk.gov.dvsa.motr.event;

import uk.gov.dvsa.motr.eventlog.Event;

public class StatusReportEvent extends Event {

    @Override
    public String getCode() {
        return "STATUS-REPORT";
    }

    public StatusReportEvent setNumberOfPermanentlyFailedNotifications(int count) {

        params.put("permanently-failed", count);
        return this;
    }

    public StatusReportEvent setNumberOfSmsPermanentlyFailedNotifications(int count) {

        params.put("sms-permanently-failed", count);
        return this;
    }

    public StatusReportEvent setNumberOfTemporaryFailedNotifications(int count) {

        params.put("temporary-failure", count);
        return this;
    }

    public StatusReportEvent setNumberOfSmsTemporaryFailedNotifications(int count) {

        params.put("sms-temporary-failure", count);
        return this;
    }

    public StatusReportEvent setNumberOfTechnicalFailedNotifications(int count) {

        params.put("technical-failure", count);
        return this;
    }

    public StatusReportEvent setNumberOfSmsTechnicalFailedNotifications(int count) {

        params.put("sms-technical-failure", count);
        return this;
    }

}
