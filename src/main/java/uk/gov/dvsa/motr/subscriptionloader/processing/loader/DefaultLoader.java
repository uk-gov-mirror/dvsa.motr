package uk.gov.dvsa.motr.subscriptionloader.processing.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.subscriptionloader.event.LoadingError;
import uk.gov.dvsa.motr.subscriptionloader.event.LoadingSuccess;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.DispatchResult;
import uk.gov.dvsa.motr.subscriptionloader.processing.dispatcher.Dispatcher;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.subscriptionloader.processing.producer.SubscriptionProducer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import static java.util.Arrays.asList;

public class DefaultLoader implements Loader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLoader.class);
    private SubscriptionProducer producer;
    private Dispatcher dispatcher;

    @Inject
    public DefaultLoader(SubscriptionProducer producer, Dispatcher dispatcher) {

        this.producer = producer;
        this.dispatcher = dispatcher;
    }

    public void run(LocalDate today)  {

        LoadReport report = new LoadReport();
        Iterator<Subscription> subscriptionIterator = producer.getIterator(asList(today.plusDays(14), today.plusMonths(1)));
        List<DispatchResult> inFlightOps = new ArrayList<>();

        try {
            report.startProcessing();
            Runtime runtime = Runtime.getRuntime();

            logger.info("free: {}, total: {}, max: {}, cpu(s): {}",
                    runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory(), runtime.availableProcessors());

            while (subscriptionIterator.hasNext()) {

                Subscription subscription = subscriptionIterator.next();
                report.incrementSubmittedForProcessing();

                DispatchResult futureResult = dispatcher.dispatch(subscription);

                inFlightOps.add(futureResult);
                reportFinished(inFlightOps, report);
            }

            reportRemaining(inFlightOps, report);

            EventLogger.logEvent(new LoadingSuccess()
                    .setProcessed(report.getProcessed())
                    .setSubmittedForProcessing(report.getSubmittedForProcessing())
                    .setDuration(report.getDuration())
            );

        } catch (Exception e) {
            EventLogger.logErrorEvent(new LoadingError(), e);
        }
    }

    private void reportFinished(List<DispatchResult> result, LoadReport report) throws LoadingException {

        Iterator<DispatchResult> iterator = result.iterator();
        processResults(iterator, report);
    }

    private void reportRemaining(List<DispatchResult> result, LoadReport report) throws Exception {

        while (result.size() > 0) {

            Iterator<DispatchResult> iterator = result.iterator();
            processResults(iterator, report);
            Thread.sleep(100);
        }
    }

    private void processResults(Iterator<DispatchResult> iterator, LoadReport report) throws LoadingException {

        while (iterator.hasNext()) {

            DispatchResult dispatchResult = iterator.next();
            if (dispatchResult.isDone()) {

                if (!dispatchResult.isFailed()) {

                    report.incrementProcessed();
                    iterator.remove();

                } else {

                    throw new LoadingException(dispatchResult.getError());
                }
            }
        }
    }
}
