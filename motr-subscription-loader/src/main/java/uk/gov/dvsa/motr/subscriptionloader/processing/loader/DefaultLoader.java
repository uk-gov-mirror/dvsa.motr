package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.subscriptionloader.event.ItemSuccess;
import uk.gov.dvsa.motr.subscriptionloader.event.LoadingError;
import uk.gov.dvsa.motr.subscriptionloader.event.LoadingSuccess;
import uk.gov.dvsa.motr.subscriptionloader.event.LoadingTimeout;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.DispatchResult;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.Dispatcher;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionCriteria;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionProducer;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class DefaultLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLoader.class.getSimpleName());

    private static final int ONE_MONTH_AHEAD_NOTIFICATION_TIME_DAYS = 30;
    private static final int TWO_WEEKS_AHEAD_NOTIFICATION_TIME_DAYS = 14;
    private static final int ONE_DAY_AFTER_NOTIFICATION_TIME_DAYS = -1;
    private static final int TWO_MONTHS_AHEAD_NOTIFICATION_TIME_DAYS = 60;

    /**
     * Time in milliseconds used define a threshold beyond which execution has timed out.
     */
    private static final int LOADING_CUTOFF_DELTA_MS = 2000;

    private SubscriptionProducer producer;
    private Dispatcher dispatcher;

    @Inject
    public DefaultLoader(SubscriptionProducer producer, Dispatcher dispatcher) {

        this.producer = producer;
        this.dispatcher = dispatcher;
    }

    public LoadReport run(LocalDate referenceDate, Context context) throws Exception {
        LoadReport report = new LoadReport();

        // TODO flag to use/support indexes
        boolean hgvPsvFeatureOn = false;

        Iterator<Subscription> subscriptionIterator;
        if (!hgvPsvFeatureOn) {
            subscriptionIterator = loadMotSubscriptions(referenceDate);
        } else {
            subscriptionIterator = loadSubscriptions(referenceDate);
        }
        List<DispatchResult> inFlightOps = new ArrayList<>();
        try {
            report.startProcessing();
            Runtime runtime = Runtime.getRuntime();

            logger.debug("free: {}, total: {}, max: {}, cpu(s): {}",
                    runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory(), runtime.availableProcessors());

            while (subscriptionIterator.hasNext()) {
                Subscription subscription = subscriptionIterator.next();
                subscription.setLoadedOnDate(referenceDate);
                report.incrementSubmittedForProcessing();
                DispatchResult futureResult = dispatcher.dispatch(subscription);
                inFlightOps.add(futureResult);
                reportFinished(inFlightOps, report, context);
            }

            reportRemaining(inFlightOps, report, context);

            EventLogger.logEvent(new LoadingSuccess()
                    .setProcessed(report.getTotalProcessed())
                    .setDuration(report.getDuration())
            );

        } catch (Exception e) {
            EventLogger.logErrorEvent(new LoadingError().setProcessed(report.getTotalProcessed()), e);
            throw e;
        }
        return report;
    }

    private Iterator<Subscription> loadMotSubscriptions(LocalDate referenceDate) {
        LocalDate oneMonthAhead = referenceDate.plusDays(ONE_MONTH_AHEAD_NOTIFICATION_TIME_DAYS);
        LocalDate twoWeeksAhead = referenceDate.plusDays(TWO_WEEKS_AHEAD_NOTIFICATION_TIME_DAYS);
        LocalDate oneDayBehind = referenceDate.plusDays(ONE_DAY_AFTER_NOTIFICATION_TIME_DAYS);

        logger.info("Reference date: {}, +14 days is {}, +1 month (30 days) is {}, -1 day is {}", referenceDate, twoWeeksAhead,
                oneMonthAhead, oneDayBehind);

        return producer.searchSubscriptions(oneMonthAhead, twoWeeksAhead, oneDayBehind);
    }

    private Iterator<Subscription> loadSubscriptions(LocalDate referenceDate) {
        LocalDate inTwoMonths = referenceDate.plusDays(TWO_MONTHS_AHEAD_NOTIFICATION_TIME_DAYS);
        LocalDate inOneMonth = referenceDate.plusDays(ONE_MONTH_AHEAD_NOTIFICATION_TIME_DAYS);
        LocalDate inTwoWeeks = referenceDate.plusDays(TWO_WEEKS_AHEAD_NOTIFICATION_TIME_DAYS);
        LocalDate yesterday = referenceDate.plusDays(ONE_DAY_AFTER_NOTIFICATION_TIME_DAYS);

        logger.info("Reference date: {}, +14 days is {}, +1 month (30 days) is {}, +2 months (60 days) is {}, -1 day is {}",
                referenceDate, inTwoWeeks, inOneMonth, inTwoMonths, yesterday);

        List<SubscriptionCriteria> criteria = Arrays.asList(
                new SubscriptionCriteria("MOT reminder one month in advance", inOneMonth, VehicleType.MOT),
                new SubscriptionCriteria("MOT reminder two weeks in advance", inTwoMonths, VehicleType.MOT),
                new SubscriptionCriteria("MOT reminder one day after", yesterday, VehicleType.MOT),
                new SubscriptionCriteria("HGV reminder two months in advance", inTwoMonths, VehicleType.HGV),
                new SubscriptionCriteria("HGV reminder one month in advance", inOneMonth, VehicleType.HGV),
                new SubscriptionCriteria("HGV reminder two months in advance", inTwoMonths, VehicleType.PSV),
                new SubscriptionCriteria("HGV reminder one month in advance", inOneMonth, VehicleType.PSV)
        );

        return producer.searchSubscriptions(criteria);
    }

    private void reportFinished(List<DispatchResult> result, LoadReport report, Context context) throws LoadingException {

        checkRemainingTime(report, context);
        Iterator<DispatchResult> iterator = result.iterator();
        processResults(iterator, report);
    }

    private void reportRemaining(List<DispatchResult> result, LoadReport report, Context context) throws Exception {

        logger.debug("reporting remaining count: {}", result.size());
        while (result.size() > 0) {
            checkRemainingTime(report, context);
            Iterator<DispatchResult> iterator = result.iterator();
            processResults(iterator, report);
        }
    }

    private void processResults(Iterator<DispatchResult> dispatchResultIterator, LoadReport report) throws LoadingException {

        while (dispatchResultIterator.hasNext()) {

            DispatchResult dispatchResult = dispatchResultIterator.next();
            if (dispatchResult.isDone()) {
                if (!dispatchResult.isFailed()) {
                    report.incrementTotalProcessed();
                    dispatchResultIterator.remove();
                    Subscription subscription = dispatchResult.getSubscription();

                    if (Strings.isNullOrEmpty(subscription.getMotTestNumber())) {
                        report.incrementDvlaVehiclesProcessed();
                        EventLogger.logEvent(new ItemSuccess()
                                .setVrm(subscription.getVrm())
                                .setEmail(subscription.getContactDetail().getValue())
                                .setContactType(subscription.getContactDetail().getContactType().getValue())
                                .setDvlaId(subscription.getDvlaId())
                                .setDueDate(subscription.getMotDueDate())
                                .setId(subscription.getId())
                        );
                    } else {
                        report.incrementNonDvlaVehiclesProcessed();
                        EventLogger.logEvent(new ItemSuccess()
                                .setVrm(subscription.getVrm())
                                .setEmail(subscription.getContactDetail().getValue())
                                .setContactType(subscription.getContactDetail().getContactType().getValue())
                                .setMotTestNumber(subscription.getMotTestNumber())
                                .setDueDate(subscription.getMotDueDate())
                                .setId(subscription.getId())
                        );
                    }
                } else {
                    throw new LoadingException(dispatchResult.getError());
                }
            }
        }
    }

    private void checkRemainingTime(LoadReport report, Context context) throws LoadingException {

        if (context.getRemainingTimeInMillis() < LOADING_CUTOFF_DELTA_MS) {
            EventLogger.logEvent(new LoadingTimeout()
                    .setProcessed(report.getTotalProcessed())
                    .setSubmittedForProcessing(report.getSubmittedForProcessing())
                    .setDuration(report.getDuration()));
            throw new LoadingException(new Exception("Ran out of time. Unable to process all subscriptions"));
        }
    }
}
