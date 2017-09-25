package uk.gov.dvsa.motr.performance;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.event.AllBouncingEmailsProcessedEvent;
import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.report.BouncingEmailCleanerReport;

@Aspect
public class PerfAspect {

    public static MetricRegistry metricRegistry = new MetricRegistry();

    private static final Logger logger = LoggerFactory.getLogger(PerfAspect.class);

    private Timer queryOutComeTimer = metricRegistry.timer("query_dynamodb_timer");
    private Timer deleteRecordTimer = metricRegistry.timer("delete_record_timer");
    private Timer cancelSubscriptionTimer = metricRegistry.timer("cancel_subscription_timer");

    @Around("execution(* uk.gov.dvsa.motr.persistence.repository.SubscriptionRepository.scanOutcomeItemCollection(..))")
    public Object scan(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object response;

        Timer.Context dynamoDbContext = queryOutComeTimer.time();

        try {
            response = proceedingJoinPoint.proceed();
        } finally {
            dynamoDbContext.stop();
        }

        return response;
    }

    @Around("execution(* uk.gov.dvsa.motr.persistence.repository.SubscriptionRepository.deleteRecord(..))")
    public Object deleteRecord(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object response;

        Timer.Context deleteRecordContext = deleteRecordTimer.time();

        try {
            response = proceedingJoinPoint.proceed();
        } finally {
            deleteRecordContext.stop();
        }

        return response;
    }

    @Around("execution(* uk.gov.dvsa.motr.persistence.repository.CancelledSubscriptionRepository.cancelSubscription(..))")
    public Object unsubscribe(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object response;

        Timer.Context unsubscribeContext = cancelSubscriptionTimer.time();

        try {
            response = proceedingJoinPoint.proceed();
        } finally {
            unsubscribeContext.stop();
        }

        return response;
    }

    @Around("execution(* uk.gov.dvsa.motr.service.UnsubscribeBouncingContactDetailsService.run(..))")
    public Object runMetrics(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        BouncingEmailCleanerReport response;

        Timer.Context dynamoDbContext = queryOutComeTimer.time();

        try {
            response = (BouncingEmailCleanerReport) proceedingJoinPoint.proceed();

        } finally {
            dynamoDbContext.stop();
            EventLogger.logEvent(new AllBouncingEmailsProcessedEvent()
                    .setDeleteRecord99thPercentile(deleteRecordTimer.getSnapshot().get99thPercentile())
                    .setDeleteRecord95thPercentile(deleteRecordTimer.getSnapshot().get95thPercentile())
                    .setDeleteRecord75thPercentile(deleteRecordTimer.getSnapshot().get75thPercentile())
                    .setDeleteRecordFetchCountOfCalls(deleteRecordTimer.getCount())
                    .setDeleteRecordFetchMax(deleteRecordTimer.getSnapshot().getMax())
                    .setDeleteRecordFetchMin(deleteRecordTimer.getSnapshot().getMin())
                    .setDeleteRecordStdDeviation(deleteRecordTimer.getSnapshot().getStdDev())
                    .setCancelSubscription99thPercentile(cancelSubscriptionTimer.getSnapshot().get99thPercentile())
                    .setCancelSubscription95thPercentile(cancelSubscriptionTimer.getSnapshot().get95thPercentile())
                    .setCancelSubscription75thPercentile(cancelSubscriptionTimer.getSnapshot().get75thPercentile())
                    .setCancelSubscriptionCountOfCalls(cancelSubscriptionTimer.getCount())
                    .setCancelSubscriptionFetchMax(cancelSubscriptionTimer.getSnapshot().getMax())
                    .setCancelSubscriptionFetchMin(cancelSubscriptionTimer.getSnapshot().getMin())
                    .setCancelSubscriptionStdDeviation(cancelSubscriptionTimer.getSnapshot().getStdDev())
                    .setScan99thPercentile(queryOutComeTimer.getSnapshot().get99thPercentile())
                    .setQuery95thPercentile(queryOutComeTimer.getSnapshot().get95thPercentile())
                    .setQuery75thPercentile(queryOutComeTimer.getSnapshot().get75thPercentile())
                    .setQueryFetchCountOfCalls(queryOutComeTimer.getCount())
                    .setQueryFetchMax(queryOutComeTimer.getSnapshot().getMax())
                    .setQueryFetchMin(queryOutComeTimer.getSnapshot().getMin())
                    .setQueryStdDeviation(queryOutComeTimer.getSnapshot().getStdDev()));

        }

        return response;
    }
}
