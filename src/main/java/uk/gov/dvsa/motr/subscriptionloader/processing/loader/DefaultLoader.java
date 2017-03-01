package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import com.amazonaws.services.lambda.runtime.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.subscriptionloader.event.LoadingError;
import uk.gov.dvsa.motr.subscriptionloader.event.LoadingSuccess;
import uk.gov.dvsa.motr.subscriptionloader.event.LoadingTimeout;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.DispatchResult;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.Dispatcher;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionProducer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class DefaultLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLoader.class);
    private static final int FIRST_NOTIFICATION_TIME_MONTHS = 1;
    private static final int SECOND_NOTIFICATION_TIME_DAYS = 14;

    private SubscriptionProducer producer;
    private Dispatcher dispatcher;

    @Inject
    public DefaultLoader(SubscriptionProducer producer, Dispatcher dispatcher) {

        this.producer = producer;
        this.dispatcher = dispatcher;
    }

    public LoadReport run(LocalDate today, Context context) throws Exception {

        LoadReport report = new LoadReport();
        Iterator<Subscription> subscriptionIterator = producer.getIterator(today.plusMonths(FIRST_NOTIFICATION_TIME_MONTHS),
                today.plusDays(SECOND_NOTIFICATION_TIME_DAYS));
        List<DispatchResult> inFlightOps = new ArrayList<>();

        try {
            report.startProcessing();
            Runtime runtime = Runtime.getRuntime();

            logger.debug("free: {}, total: {}, max: {}, cpu(s): {}",
                    runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory(), runtime.availableProcessors());

            while (subscriptionIterator.hasNext()) {
                Subscription subscription = subscriptionIterator.next();
                report.incrementSubmittedForProcessing();
                DispatchResult futureResult = dispatcher.dispatch(subscription);
                inFlightOps.add(futureResult);
                reportFinished(inFlightOps, report, context);
            }

            reportRemaining(inFlightOps, report, context);

            EventLogger.logEvent(new LoadingSuccess()
                    .setProcessed(report.getProcessed())
                    .setSubmittedForProcessing(report.getSubmittedForProcessing())
                    .setDuration(report.getDuration())
            );

        } catch (Exception e) {
            EventLogger.logErrorEvent(new LoadingError(), e);
            throw e;
        }
        return report;
    }

    private void reportFinished(List<DispatchResult> result, LoadReport report, Context context) throws LoadingException {

        checkRemainingTime(report, context);
        Iterator<DispatchResult> iterator = result.iterator();
        processResults(iterator, report);
    }

    private void reportRemaining(List<DispatchResult> result, LoadReport report, Context context) throws Exception {

        logger.debug("reporting remaining with result.size of {}", result.size());
        while (result.size() > 0) {
            checkRemainingTime(report, context);
            Iterator<DispatchResult> iterator = result.iterator();
            processResults(iterator, report);
            Thread.sleep(100);
        }
    }

    private void processResults(Iterator<DispatchResult> dispatchResultIterator, LoadReport report) throws LoadingException {

        while (dispatchResultIterator.hasNext()) {

            DispatchResult dispatchResult = dispatchResultIterator.next();
            if (dispatchResult.isDone()) {
                if (!dispatchResult.isFailed()) {
                    report.incrementProcessed();
                    dispatchResultIterator.remove();
                } else {
                    throw new LoadingException(dispatchResult.getError());
                }
            }
        }
    }

    private void checkRemainingTime(LoadReport report, Context context) throws LoadingException {

        if (context.getRemainingTimeInMillis() < 2000) {
            EventLogger.logEvent(new LoadingTimeout()
                    .setProcessed(report.getProcessed())
                    .setSubmittedForProcessing(report.getSubmittedForProcessing())
                    .setDuration(report.getDuration()));
            throw new LoadingException(new Exception("Ran out of time. Unable to process all subscriptions"));
        }
    }
}
