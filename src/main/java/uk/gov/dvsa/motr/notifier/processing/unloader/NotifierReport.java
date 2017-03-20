package uk.gov.dvsa.motr.notifier.processing.unloader;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;

import uk.gov.dvsa.motr.notifier.processing.performance.MetricsTimerWrapper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NotifierReport {

    private AtomicInteger successfullyProcessed = new AtomicInteger(0);

    private AtomicInteger failedToProcess = new AtomicInteger(0);

    private AtomicLong allMessagesStartTime = new AtomicLong(0);

    private Timer vehicleDetailsTimer;

    private Timer sendEmailTimer;

    private Timer updateExpiryDateTimer;

    public void incrementSuccessfullyProcessed() {

        successfullyProcessed.incrementAndGet();
    }

    public void incrementFailedToProcess() {

        failedToProcess.incrementAndGet();
    }

    public int getFailedToProcess() {

        return failedToProcess.get();
    }

    public int getSuccessfullyProcessed() {

        return successfullyProcessed.get();
    }

    public void startProcessingTheMessages() {

        allMessagesStartTime = new AtomicLong(System.currentTimeMillis());
    }

    public long getDurationToProcessTheMessages() {

        return System.currentTimeMillis() - allMessagesStartTime.get();
    }

    public MetricsTimerWrapper getVehicleDetailsTimer() {
        return new MetricsTimerWrapper(vehicleDetailsTimer);
    }

    public void setVehicleDetailsTimer(Timer vehicleDetailsTimer) {
        this.vehicleDetailsTimer = vehicleDetailsTimer;
    }

    public MetricsTimerWrapper getSendEmailTimer() {
        return new MetricsTimerWrapper(sendEmailTimer);
    }

    public void setSendEmailTimer(Timer sendEmailTimer) {
        this.sendEmailTimer = sendEmailTimer;
    }

    public MetricsTimerWrapper getUpdateExpiryDateTimer() {
        return new MetricsTimerWrapper(updateExpiryDateTimer);
    }

    public void setUpdateExpiryDateTimer(Timer updateExpiryDateTimer) {
        this.updateExpiryDateTimer = updateExpiryDateTimer;
    }
}
