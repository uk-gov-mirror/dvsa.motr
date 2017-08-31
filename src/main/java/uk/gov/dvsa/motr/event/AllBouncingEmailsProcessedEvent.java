package uk.gov.dvsa.motr.event;

import uk.gov.dvsa.motr.eventlog.Event;

public class AllBouncingEmailsProcessedEvent extends Event {

    @Override
    public String getCode() {
        return "ALL-BOUNCING-EMAILS-PROCESSED";
    }

    public AllBouncingEmailsProcessedEvent setDurationOfCleanUp(long durationOfCleanUp) {

        params.put("duration-of-clean-up-ms", durationOfCleanUp);

        return this;
    }

    public AllBouncingEmailsProcessedEvent setNumberOfSubscriptionsCancelled(long numberOfSubscriptionsCancelled) {

        params.put("number-of-subscriptions-cancelled", numberOfSubscriptionsCancelled);

        return this;
    }

    public AllBouncingEmailsProcessedEvent setDeleteRecord99thPercentile(double metric) {

        params.put("ninety-ninth-percentile-delete-record-fetch", metric);

        return this;
    }

    public AllBouncingEmailsProcessedEvent setDeleteRecord95thPercentile(double metric) {

        params.put("ninety-fifth-percentile-delete-record-fetch", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setDeleteRecord75thPercentile(double metric) {

        params.put("seventy-fifth-percentile-delete-record-fetch", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setDeleteRecordFetchCountOfCalls(long metric) {

        params.put("count-of-delete-record-fetch-calls", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setDeleteRecordFetchMax(long metric) {

        params.put("max-time-delete-record-fetch-call", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setDeleteRecordFetchMin(long metric) {

        params.put("min-time-delete-record-fetch-call", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setDeleteRecordStdDeviation(double metric) {

        params.put("std-deviation-delete-record-fetch-call", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setCancelSubscription99thPercentile(double metric) {

        params.put("ninety-ninth-percentile-cancel-subscription-calls", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setCancelSubscription95thPercentile(double metric) {

        params.put("ninety-fifth-percentile-cancel-subscription", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setCancelSubscription75thPercentile(double metric) {

        params.put("seventy-fifth-percentile-cancel-subscription", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setCancelSubscriptionStdDeviation(double metric) {

        params.put("std-deviation-cancel-subscription-call", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setCancelSubscriptionCountOfCalls(long metric) {

        params.put("count-of-cancel-subscription-calls", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setCancelSubscriptionFetchMax(long metric) {

        params.put("max-time-delete-record-fetch-call", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setCancelSubscriptionFetchMin(long metric) {

        params.put("min-time-delete-record-fetch-call", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setScan99thPercentile(double metric) {

        params.put("ninety-ninth-percentile-delete-record-fetch", metric);

        return this;
    }

    public AllBouncingEmailsProcessedEvent setQuery95thPercentile(double metric) {

        params.put("ninety-fifth-percentile-query-fetch", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setQuery75thPercentile(double metric) {

        params.put("seventy-fifth-percentile-query-fetch", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setQueryFetchCountOfCalls(long metric) {

        params.put("count-of-query-fetch-calls", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setQueryFetchMax(long metric) {

        params.put("max-time-query-fetch-call", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setQueryFetchMin(long metric) {

        params.put("min-time-query-fetch-call", metric);
        return this;
    }

    public AllBouncingEmailsProcessedEvent setQueryStdDeviation(double metric) {

        params.put("std-deviation-query-fetch-call", metric);
        return this;
    }
}
