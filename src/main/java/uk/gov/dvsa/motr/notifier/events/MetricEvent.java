package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class MetricEvent extends Event {

    @Override
    public String getCode() {

        return "METRICS-STATISTICS";
    }

    public MetricEvent setVehicleDetails99thPercentile(double metric) {

        params.put("ninety-ninth-percentile-vehicle-details-fetch", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetails95thPercentile(double metric) {

        params.put("ninety-fifth-percentile-vehicle-details-fetch", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetails75thPercentile(double metric) {

        params.put("seventy-fifth-percentile-vehicle-details-fetch", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsFetchCountOfCalls(long metric) {

        params.put("count-of-vehicle-details-fetch-calls", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsFetchMax(long metric) {

        params.put("max-time-vehicle-details-fetch-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsFetchMin(long metric) {

        params.put("min-time-vehicle-details-fetch-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsStdDeviation(double metric) {

        params.put("std-deviation-vehicle-details-fetch-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setSendEmail99thPercentile(double metric) {

        params.put("ninety-ninth-percentile-send-email-calls", String.valueOf(metric));
        return this;
    }

    public MetricEvent setSendEmail95thPercentile(double metric) {

        params.put("ninety-fifth-percentile-send-email", String.valueOf(metric));
        return this;
    }

    public MetricEvent setSendEmail75thPercentile(double metric) {

        params.put("seventy-fifth-percentile-send-email", String.valueOf(metric));
        return this;
    }

    public MetricEvent setSendEmailMax(long metric) {

        params.put("max-time-send-email-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setSendEmailMin(long metric) {

        params.put("min-time-send-email-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setSendEmailStdDeviation(double metric) {

        params.put("std-deviation-send-email-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setSendEmailCountOfCalls(long metric) {

        params.put("count-of-send-email-calls", String.valueOf(metric));
        return this;
    }

    public MetricEvent setExpiryDateUpdate99thPercentile(double metric) {

        params.put("ninety-ninth-percentile-expiry-date-updates", String.valueOf(metric));
        return this;
    }

    public MetricEvent setExpiryDateUpdate95thPercentile(double metric) {

        params.put("ninety-fifth-percentile-expiry-date-update", String.valueOf(metric));
        return this;
    }

    public MetricEvent setExpiryDateUpdate75thPercentile(double metric) {

        params.put("seventy-fifth-percentile-expiry-date-update", String.valueOf(metric));
        return this;
    }

    public MetricEvent setExpiryDateUpdateMax(long metric) {

        params.put("max-time-expiry-date-update-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setExpiryDateUpdateMin(long metric) {

        params.put("min-time-expiry-date-update-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setExpiryDateUpdateStdDeviation(double metric) {

        params.put("std-deviation-expiry-date-update-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setExpiryDateUpdateCountOfCalls(long metric) {

        params.put("count-of-expiry-date-update-calls", String.valueOf(metric));
        return this;
    }
}
