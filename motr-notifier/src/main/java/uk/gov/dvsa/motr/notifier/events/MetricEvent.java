package uk.gov.dvsa.motr.notifier.events;

import uk.gov.dvsa.motr.eventlog.Event;

public class MetricEvent extends Event {

    @Override
    public String getCode() {

        return "METRICS-STATISTICS";
    }

    public MetricEvent setVehicleDetails99thPercentileFetchByMotTestNumber(double metric) {

        params.put("ninety-ninth-percentile-vehicle-details-fetchByMotTestNumber", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetails99thPercentileFetchByDvlaId(double metric) {

        params.put("ninety-ninth-percentile-vehicle-details-fetchByDvlaId", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetails95thPercentileFetchByMotTestNumber(double metric) {

        params.put("ninety-fifth-percentile-vehicle-details-fetchByMotTestNumber", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetails95thPercentileFetchByDvlaId(double metric) {

        params.put("ninety-fifth-percentile-vehicle-details-fetchByDvlaId", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetails75thPercentileFetchByMotTestNumber(double metric) {

        params.put("seventy-fifth-percentile-vehicle-details-fetchByMotTestNumber", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetails75thPercentileFetchByDvlaId(double metric) {

        params.put("seventy-fifth-percentile-vehicle-details-fetchByDvlaId", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsFetchCountOfCallsFetchByMotTestNumber(long metric) {

        params.put("count-of-vehicle-details-fetchByMotTestNumber-calls", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsFetchCountOfCallsFetchByDvlaId(long metric) {

        params.put("count-of-vehicle-details-fetchByDvlaIdr-calls", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsFetchMaxFetchByMotTestNumber(long metric) {

        params.put("max-time-vehicle-details-fetchByMotTestNumber-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsFetchMaxFetchByDvlaId(long metric) {

        params.put("max-time-vehicle-details-fetchByDvlaId-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsFetchMinFetchByMotTestNumber(long metric) {

        params.put("min-time-vehicle-details-fetchByMotTestNumber-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsFetchMinFetchByDvlaId(long metric) {

        params.put("min-time-vehicle-details-fetchByDvlaId-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsStdDeviationFetchByMotTestNumber(double metric) {

        params.put("std-deviation-vehicle-details-fetchByMotTestNumber-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setVehicleDetailsStdDeviationFetchByDvlaId(double metric) {

        params.put("std-deviation-vehicle-details-fetchByDvlaId-call", String.valueOf(metric));
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

    public MetricEvent setProcessItem99thPercentile(double metric) {

        params.put("ninety-ninth-percentile-process-item-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setProcessItem95thPercentile(double metric) {

        params.put("ninety-fifth-percentile-process-item-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setProcessItem75thPercentile(double metric) {

        params.put("seventy-fifth-percentile-process-item-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setProcessItemMax(long metric) {

        params.put("max-time-process-item-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setProcessItemMin(long metric) {

        params.put("min-time-process-item-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setProcessItemStdDeviation(double metric) {

        params.put("std-deviation-process-item-call", String.valueOf(metric));
        return this;
    }

    public MetricEvent setProcessItemCountOfCalls(long metric) {

        params.put("count-of-process-item-calls", String.valueOf(metric));
        return this;
    }
}
