package uk.gov.dvsa.motr.notifier.processing.unloader;

import com.amazonaws.services.lambda.runtime.Context;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.executor.BlockingExecutor;
import uk.gov.dvsa.motr.notifier.events.RemindersProcessedEvent;
import uk.gov.dvsa.motr.notifier.events.UnloadingTimedOutEvent;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.performance.MetricsPerfAspect;
import uk.gov.dvsa.motr.notifier.processing.queue.QueueItemRemover;
import uk.gov.dvsa.motr.notifier.processing.queue.SubscriptionsReceiver;
import uk.gov.dvsa.motr.notifier.processing.service.ProcessSubscriptionService;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class QueueUnloader {

    private SubscriptionsReceiver subscriptionsReceiver;
    private QueueItemRemover queueItemRemover;
    private BlockingExecutor executor;
    private ProcessSubscriptionService processSubscriptionService;
    private int remaingingTimeThresholdMs;
    private int postProcessingDelayMs;

    public QueueUnloader(
            SubscriptionsReceiver subscriptionsReceiver,
            QueueItemRemover queueItemRemover,
            BlockingExecutor executor,
            ProcessSubscriptionService processSubscriptionService,
            int remaingingTimeThresholdMs,
            int postProcessingDelayMs) {

        this.subscriptionsReceiver = subscriptionsReceiver;
        this.queueItemRemover = queueItemRemover;
        this.executor = executor;
        this.remaingingTimeThresholdMs = remaingingTimeThresholdMs;
        this.processSubscriptionService = processSubscriptionService;
        this.postProcessingDelayMs = postProcessingDelayMs;
    }

    public NotifierReport run(Context context) {

        NotifierReport report = new NotifierReport();
        report.startProcessingTheMessages();

        for (SubscriptionQueueItem subscriptionQueueItemFromQueue : subscriptionsReceiver) {

            if (passedTimeoutThreshold(report, context)) {
                break;
            }

            LocalDate requestDate = subscriptionQueueItemFromQueue.getLoadedOnDate();

            executor.execute(new ProcessSubscriptionTask(
                    requestDate, subscriptionQueueItemFromQueue, report, processSubscriptionService, queueItemRemover));
        }

        try {
            executor.awaitTermination(postProcessingDelayMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EventLogger.logEvent(new RemindersProcessedEvent()
                .setAmountOfMessagesSuccessfullyProcessed(report.getSuccessfullyProcessed())
                .setDurationToProcessAllMessages(report.getDurationToProcessTheMessages())
                .setAmountOfMessagesFailedToProcess(report.getFailedToProcess()));

        return report;
    }

    private boolean passedTimeoutThreshold(NotifierReport report, Context context) {

        boolean passedThreshold = false;
        if (context.getRemainingTimeInMillis() < remaingingTimeThresholdMs) {
            EventLogger.logEvent(new UnloadingTimedOutEvent()
                    .setProcessed(report.getSuccessfullyProcessed())
                    .setDuration(report.getDurationToProcessTheMessages()));
            passedThreshold = true;
        }
        return passedThreshold;
    }
}
