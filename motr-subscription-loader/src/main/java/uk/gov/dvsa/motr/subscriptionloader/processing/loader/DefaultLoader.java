package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notify.PreservationDateChecker;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

public class DefaultLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLoader.class.getSimpleName());

    private static final int ONE_MONTH_AHEAD_NOTIFICATION_TIME_MONTH = 1;
    private static final int TWO_WEEKS_AHEAD_NOTIFICATION_TIME_DAYS = 14;
    private static final int ONE_DAY_AFTER_NOTIFICATION_TIME_DAYS = -1;
    private static final int TWO_MONTHS_AHEAD_NOTIFICATION_TIME_DAYS = 60;
    private static final int ONE_MONTH_AHEAD_NOTIFICATION_TIME_29_DAYS = 29;
    private static final int ONE_MONTH_AHEAD_NOTIFICATION_TIME_30_DAYS = 30;
    private static final int ONE_MONTH_AHEAD_NOTIFICATION_TIME_31_DAYS = 31;

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

        Iterator<Subscription> subscriptionIterator = loadSubscriptions(referenceDate);
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

    private Iterator<Subscription> loadSubscriptions(LocalDate referenceDate) {
        LocalDate inTwoMonths = referenceDate.plusDays(TWO_MONTHS_AHEAD_NOTIFICATION_TIME_DAYS);
        LocalDate inOneMonth = referenceDate.plusMonths(ONE_MONTH_AHEAD_NOTIFICATION_TIME_MONTH);
        LocalDate inTwoWeeks = referenceDate.plusDays(TWO_WEEKS_AHEAD_NOTIFICATION_TIME_DAYS);
        LocalDate yesterday = referenceDate.plusDays(ONE_DAY_AFTER_NOTIFICATION_TIME_DAYS);

        logger.info("Reference date: {}, +14 days is {}, +1 month is {}, +2 months (60 days) is {}, -1 day is {}",
                referenceDate, inTwoWeeks, inOneMonth, inTwoMonths, yesterday);

        LinkedList<SubscriptionCriteria> criteria = new LinkedList<>();
        criteria.add(new SubscriptionCriteria(inTwoWeeks, VehicleType.MOT));
        criteria.add(new SubscriptionCriteria(yesterday, VehicleType.MOT));
        criteria.add(new SubscriptionCriteria(inTwoMonths, VehicleType.HGV));
        criteria.add(new SubscriptionCriteria(inTwoMonths, VehicleType.PSV));
        criteria.add(new SubscriptionCriteria(inTwoMonths, VehicleType.TRAILER));

        if (PreservationDateChecker.dateIs29February(referenceDate)) {
            return producer.searchSubscriptions(getCriteria29February(referenceDate, criteria));
        }

        if (PreservationDateChecker.dateIs28FebruaryButNotLeapYear(referenceDate)) {
            return producer.searchSubscriptions(getCriteria28FebruaryNotLeapYear(referenceDate, criteria));
        }

        if (PreservationDateChecker.expiryMonthIsLongerThanPreviousMonthButNotMarch(referenceDate)) {
            return producer.searchSubscriptions(getCriteriaShortMonth(referenceDate, criteria));
        }

        if (PreservationDateChecker.isValidPreservationDate(referenceDate)) {
            return producer.searchSubscriptions(getCriteriaWithOneMonthNotification(referenceDate, criteria));
        }

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

                    updateReport(report, subscription);
                    logSuccess(subscription);
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

    private void updateReport(LoadReport report, Subscription subscription) {
        switch (subscription.getVehicleType()) {
            case MOT:
                if (Strings.isNullOrEmpty(subscription.getMotTestNumber())) {
                    report.incrementDvlaVehiclesProcessed();
                } else {
                    report.incrementNonDvlaVehiclesProcessed();
                }
                break;
            case HGV:
                report.incrementHgvVehiclesProcessed();
                break;
            case PSV:
                report.incrementPsvVehiclesProcessed();
                break;
            case TRAILER:
                report.incrementHgvTrailersProcessed();
                break;
            default:
                report.incrementOtherVehiclesPorcessed();
        }
    }

    private void logSuccess(Subscription subscription) {
        ItemSuccess event = new ItemSuccess()
                .setVrm(subscription.getVrm())
                .setEmail(subscription.getContactDetail().getValue())
                .setContactType(subscription.getContactDetail().getContactType().getValue())
                .setDueDate(subscription.getMotDueDate())
                .setId(subscription.getId())
                .setVehicleType(subscription.getVehicleType());

        if (!Strings.isNullOrEmpty(subscription.getMotTestNumber())) {
            event.setMotTestNumber(subscription.getMotTestNumber());
        }
        if (!Strings.isNullOrEmpty(subscription.getDvlaId())) {
            event.setDvlaId(subscription.getDvlaId());
        }
        EventLogger.logEvent(event);
    }

    private LinkedList<SubscriptionCriteria> getCriteriaWithOneMonthNotification(
            LocalDate referenceDate,
            LinkedList<SubscriptionCriteria> criteria) {

        criteria = this.addOneMonthCriteria(referenceDate, criteria);

        return criteria;
    }

    private LinkedList<SubscriptionCriteria> getCriteriaShortMonth(LocalDate referenceDate, LinkedList<SubscriptionCriteria> criteria) {

        criteria = this.addOneMonthCriteria(referenceDate, criteria);
        criteria = this.add31DayCriteria(referenceDate, criteria);

        return criteria;
    }

    private LinkedList<SubscriptionCriteria> getCriteria29February(
            LocalDate referenceDate,
            LinkedList<SubscriptionCriteria> criteria) {

        criteria = this.addOneMonthCriteria(referenceDate, criteria);
        criteria = this.add31DayCriteria(referenceDate, criteria);
        criteria = this.add30DayCriteria(referenceDate, criteria);

        return criteria;

    }

    private LinkedList<SubscriptionCriteria> getCriteria28FebruaryNotLeapYear(
            LocalDate referenceDate,
            LinkedList<SubscriptionCriteria> criteria) {

        criteria = this.addOneMonthCriteria(referenceDate, criteria);
        criteria = this.add31DayCriteria(referenceDate, criteria);
        criteria = this.add30DayCriteria(referenceDate, criteria);
        criteria = this.add29DayCriteria(referenceDate, criteria);

        return criteria;
    }

    private LinkedList<SubscriptionCriteria> add29DayCriteria(
            LocalDate referenceDate,
            LinkedList<SubscriptionCriteria> criteria) {
        LocalDate in29Days = referenceDate.plusDays(ONE_MONTH_AHEAD_NOTIFICATION_TIME_29_DAYS);

        criteria.add(new SubscriptionCriteria(in29Days, VehicleType.MOT));
        criteria.add(new SubscriptionCriteria(in29Days, VehicleType.HGV));
        criteria.add(new SubscriptionCriteria(in29Days, VehicleType.PSV));
        criteria.add(new SubscriptionCriteria(in29Days, VehicleType.TRAILER));

        return criteria;
    }

    private LinkedList<SubscriptionCriteria> add30DayCriteria(
            LocalDate referenceDate,
            LinkedList<SubscriptionCriteria> criteria) {
        LocalDate in30Days = referenceDate.plusDays(ONE_MONTH_AHEAD_NOTIFICATION_TIME_30_DAYS);

        criteria.add(new SubscriptionCriteria(in30Days, VehicleType.MOT));
        criteria.add(new SubscriptionCriteria(in30Days, VehicleType.HGV));
        criteria.add(new SubscriptionCriteria(in30Days, VehicleType.PSV));
        criteria.add(new SubscriptionCriteria(in30Days, VehicleType.TRAILER));

        return criteria;
    }

    private LinkedList<SubscriptionCriteria> add31DayCriteria(
            LocalDate referenceDate,
            LinkedList<SubscriptionCriteria> criteria) {
        LocalDate in31Days = referenceDate.plusDays(ONE_MONTH_AHEAD_NOTIFICATION_TIME_31_DAYS);

        criteria.add(new SubscriptionCriteria(in31Days, VehicleType.MOT));
        criteria.add(new SubscriptionCriteria(in31Days, VehicleType.HGV));
        criteria.add(new SubscriptionCriteria(in31Days, VehicleType.PSV));
        criteria.add(new SubscriptionCriteria(in31Days, VehicleType.TRAILER));

        return criteria;
    }

    private LinkedList<SubscriptionCriteria> addOneMonthCriteria(
            LocalDate referenceDate,
            LinkedList<SubscriptionCriteria> criteria) {
        LocalDate inOneMonth = referenceDate.plusMonths(ONE_MONTH_AHEAD_NOTIFICATION_TIME_MONTH);

        criteria.add(new SubscriptionCriteria(inOneMonth, VehicleType.MOT));
        criteria.add(new SubscriptionCriteria(inOneMonth, VehicleType.HGV));
        criteria.add(new SubscriptionCriteria(inOneMonth, VehicleType.PSV));
        criteria.add(new SubscriptionCriteria(inOneMonth, VehicleType.TRAILER));

        return criteria;
    }
}
