package uk.gov.dvsa.motr.notifier.processing.performance;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.events.MetricEvent;

@Aspect
public class MetricsPerfAspect {

    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    private Timer vehicleDetailsFetchByMotTestNumber = METRIC_REGISTRY.timer("vehicleDetailsFetchByMotTestNumber");
    private Timer vehicleDetailsFetchByDvlaId = METRIC_REGISTRY.timer("vehicleDetailsFetchByDvlaId");
    private Timer sendEmailTimer = METRIC_REGISTRY.timer("notifyService_sendEmail");
    private Timer sendSmsTimer = METRIC_REGISTRY.timer("notifyService_sendSms");
    private Timer updateExpiryDateTimer = METRIC_REGISTRY.timer("subscriptionRepository_updateExpiryDate");
    private Timer processItemTimer = METRIC_REGISTRY.timer("process_single_item");

    @Around("execution(* uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient.fetchByMotTestNumber(..))")
    public Object vehicleDetailsClientFetch(ProceedingJoinPoint joinPoint) throws Throwable {

        Object response;

        Timer.Context vehicleDetailsContext = vehicleDetailsFetchByMotTestNumber.time();
        try {
            response = joinPoint.proceed();
        } finally {
            vehicleDetailsContext.stop();
        }

        return response;
    }

    @Around("execution(* uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient.fetchByDvlaId(..))")
    public Object vehicleDetailsClientFetchByDvlaId(ProceedingJoinPoint joinPoint) throws Throwable {

        Object response;

        Timer.Context vehicleDetailsFetchByDvlaIdContext = vehicleDetailsFetchByDvlaId.time();
        try {
            response = joinPoint.proceed();
        } finally {
            vehicleDetailsFetchByDvlaIdContext.stop();
        }

        return response;
    }

    @Around("execution(* uk.gov.dvsa.motr.notifier.notify.NotifyEmailService.sendEmail(..))")
    public Object notifyServiceSendEmailCalls(ProceedingJoinPoint joinPoint) throws Throwable {

        Object response;

        Timer.Context sendEmailContext = sendEmailTimer.time();
        try {
            response = joinPoint.proceed();
        } finally {
            sendEmailContext.stop();
        }

        return response;
    }

    @Around("execution(* uk.gov.dvsa.motr.notifier.notify.NotifySmsService.sendSms(..))")
    public Object notifyServiceSendSmsCalls(ProceedingJoinPoint joinPoint) throws Throwable {

        Object response;

        Timer.Context sendSmsContext = sendSmsTimer.time();
        try {
            response = joinPoint.proceed();
        } finally {
            sendSmsContext.stop();
        }

        return response;
    }

    @Around("execution(* uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionRepository.updateExpiryDate(..))")
    public Object subscriptionRepositoryUpdateExpiryDateCalls(ProceedingJoinPoint joinPoint) throws Throwable {

        Object response;

        Timer.Context updateExpiryContext = updateExpiryDateTimer.time();
        try {
            response = joinPoint.proceed();
        } finally {
            updateExpiryContext.stop();
        }

        return response;
    }

    @Around("execution(* uk.gov.dvsa.motr.notifier.processing.service.ProcessSubscriptionService.processSubscription(..))")
    public Object processSubscriptionCalls(ProceedingJoinPoint joinPoint) throws Throwable {

        Object response;

        Timer.Context updateExpiryContext = processItemTimer.time();
        try {
            response = joinPoint.proceed();
        } finally {
            updateExpiryContext.stop();
        }

        return response;
    }

    @Before("execution(* uk.gov.dvsa.motr.notifier.handler.EventHandler.handle(..))")
    public void beginMetrics() {
        vehicleDetailsFetchByMotTestNumber = METRIC_REGISTRY.timer("vehicleDetailsFetchByMotTestNumber");
        vehicleDetailsFetchByDvlaId = METRIC_REGISTRY.timer("vehicleDetailsFetchByDvlaId");
        sendEmailTimer = METRIC_REGISTRY.timer("notifyService_sendEmail");
        sendSmsTimer = METRIC_REGISTRY.timer("notifyService_sendSms");
        updateExpiryDateTimer = METRIC_REGISTRY.timer("subscriptionRepository_updateExpiryDate");
        processItemTimer = METRIC_REGISTRY.timer("process_single_item");
    }

    @Around("execution(* uk.gov.dvsa.motr.notifier.handler.EventHandler.handle(..))")
    public Object processMetricEvent(ProceedingJoinPoint joinPoint) throws Throwable {

        Object response;

        try {
            response = joinPoint.proceed();
        } finally {
            EventLogger.logEvent(new MetricEvent()
                    .setVehicleDetails99thPercentileFetchByMotTestNumber(vehicleDetailsFetchByMotTestNumber.getSnapshot()
                    .get99thPercentile())
                    .setVehicleDetails99thPercentileFetchByDvlaId(vehicleDetailsFetchByDvlaId.getSnapshot().get99thPercentile())
                    .setVehicleDetails95thPercentileFetchByMotTestNumber(vehicleDetailsFetchByMotTestNumber.getSnapshot()
                    .get95thPercentile())
                    .setVehicleDetails95thPercentileFetchByDvlaId(vehicleDetailsFetchByDvlaId.getSnapshot().get95thPercentile())
                    .setVehicleDetails75thPercentileFetchByMotTestNumber(vehicleDetailsFetchByMotTestNumber.getSnapshot()
                    .get75thPercentile())
                    .setVehicleDetails75thPercentileFetchByDvlaId(vehicleDetailsFetchByDvlaId.getSnapshot().get75thPercentile())
                    .setVehicleDetailsFetchCountOfCallsFetchByMotTestNumber(vehicleDetailsFetchByMotTestNumber.getCount())
                    .setVehicleDetailsFetchCountOfCallsFetchByDvlaId(vehicleDetailsFetchByDvlaId.getCount())
                    .setVehicleDetailsFetchMaxFetchByMotTestNumber(vehicleDetailsFetchByMotTestNumber.getSnapshot().getMax())
                    .setVehicleDetailsFetchMaxFetchByDvlaId(vehicleDetailsFetchByDvlaId.getSnapshot().getMax())
                    .setVehicleDetailsFetchMinFetchByMotTestNumber(vehicleDetailsFetchByMotTestNumber.getSnapshot().getMin())
                    .setVehicleDetailsFetchMinFetchByDvlaId(vehicleDetailsFetchByDvlaId.getSnapshot().getMin())
                    .setVehicleDetailsStdDeviationFetchByMotTestNumber(vehicleDetailsFetchByMotTestNumber.getSnapshot().getStdDev())
                    .setVehicleDetailsStdDeviationFetchByDvlaId(vehicleDetailsFetchByDvlaId.getSnapshot().getStdDev())
                    .setSendEmailCountOfCalls(sendEmailTimer.getCount())
                    .setSendEmail99thPercentile(sendEmailTimer.getSnapshot().get99thPercentile())
                    .setSendEmail95thPercentile(sendEmailTimer.getSnapshot().get95thPercentile())
                    .setSendEmail75thPercentile(sendEmailTimer.getSnapshot().get75thPercentile())
                    .setSendEmailMax(sendEmailTimer.getSnapshot().getMax())
                    .setSendEmailMin(sendEmailTimer.getSnapshot().getMin())
                    .setSendEmailStdDeviation(sendEmailTimer.getSnapshot().getStdDev())
                    .setSendSmsCountOfCalls(sendSmsTimer.getCount())
                    .setSendSms99thPercentile(sendSmsTimer.getSnapshot().get99thPercentile())
                    .setSendSms95thPercentile(sendSmsTimer.getSnapshot().get95thPercentile())
                    .setSendSms75thPercentile(sendSmsTimer.getSnapshot().get75thPercentile())
                    .setSendSmsMax(sendSmsTimer.getSnapshot().getMax())
                    .setSendSmsMin(sendSmsTimer.getSnapshot().getMin())
                    .setSendSmsStdDeviation(sendSmsTimer.getSnapshot().getStdDev())
                    .setExpiryDateUpdateCountOfCalls(updateExpiryDateTimer.getCount())
                    .setExpiryDateUpdate99thPercentile(updateExpiryDateTimer.getSnapshot().get99thPercentile())
                    .setExpiryDateUpdate95thPercentile(updateExpiryDateTimer.getSnapshot().get95thPercentile())
                    .setExpiryDateUpdate75thPercentile(updateExpiryDateTimer.getSnapshot().get75thPercentile())
                    .setExpiryDateUpdateMax(updateExpiryDateTimer.getSnapshot().getMax())
                    .setExpiryDateUpdateMin(updateExpiryDateTimer.getSnapshot().getMin())
                    .setExpiryDateUpdateStdDeviation(updateExpiryDateTimer.getSnapshot().getStdDev())
                    .setProcessItemCountOfCalls(processItemTimer.getCount())
                    .setProcessItem99thPercentile(processItemTimer.getSnapshot().get99thPercentile())
                    .setProcessItem95thPercentile(processItemTimer.getSnapshot().get95thPercentile())
                    .setProcessItem75thPercentile(processItemTimer.getSnapshot().get75thPercentile())
                    .setProcessItemMax(processItemTimer.getSnapshot().getMax())
                    .setProcessItemMin(processItemTimer.getSnapshot().getMin())
                    .setProcessItemStdDeviation(processItemTimer.getSnapshot().getStdDev()));
        }

        return response;
    }
}
