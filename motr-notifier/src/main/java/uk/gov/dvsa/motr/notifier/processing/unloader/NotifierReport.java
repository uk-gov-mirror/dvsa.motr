package uk.gov.dvsa.motr.notifier.processing.unloader;

import com.codahale.metrics.Timer;

import uk.gov.dvsa.motr.notifier.processing.performance.MetricsTimerWrapper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NotifierReport {

    private Timer vehicleDetailsTimerFetchByMotTestNumber;

    private Timer sendEmailTimer;

    private Timer sendSmsTimer;

    private Timer updateExpiryDateTimer;

    private Timer processItemTimer;

    private Timer vehicleDetailsTimerFetchByDvlaId;

    public MetricsTimerWrapper getVehicleDetailsTimerFetchByMotTestNumber() {
        return new MetricsTimerWrapper(vehicleDetailsTimerFetchByMotTestNumber);
    }

    public void setVehicleDetailsTimerFetchByMotTestNumber(Timer vehicleDetailsTimerFetchByMotTestNumber) {
        this.vehicleDetailsTimerFetchByMotTestNumber = vehicleDetailsTimerFetchByMotTestNumber;
    }

    public MetricsTimerWrapper getVehicleDetailsTimerFetchByDvlaId() {

        return new MetricsTimerWrapper(vehicleDetailsTimerFetchByDvlaId);
    }

    public void setVehicleDetailsTimerFetchByDvlaId(Timer vehicleDetailsTimerFetchByDvlaId) {

        this.vehicleDetailsTimerFetchByDvlaId = vehicleDetailsTimerFetchByDvlaId;
    }

    public MetricsTimerWrapper getSendEmailTimer() {
        return new MetricsTimerWrapper(sendEmailTimer);
    }

    public void setSendEmailTimer(Timer sendEmailTimer) {
        this.sendEmailTimer = sendEmailTimer;
    }

    public MetricsTimerWrapper getSendSmsTimer() {

        return new MetricsTimerWrapper(sendSmsTimer);
    }

    public void setSendSmsTimer(Timer sendSmsTimer) {

        this.sendSmsTimer = sendSmsTimer;
    }

    public MetricsTimerWrapper getUpdateExpiryDateTimer() {
        return new MetricsTimerWrapper(updateExpiryDateTimer);
    }

    public void setUpdateExpiryDateTimer(Timer updateExpiryDateTimer) {
        this.updateExpiryDateTimer = updateExpiryDateTimer;
    }

    public MetricsTimerWrapper getProcessItemTimer() {
        return new MetricsTimerWrapper(processItemTimer);
    }

    public void setProcessItemTimer(Timer processItemTimer) {
        this.processItemTimer = processItemTimer;
    }
}
