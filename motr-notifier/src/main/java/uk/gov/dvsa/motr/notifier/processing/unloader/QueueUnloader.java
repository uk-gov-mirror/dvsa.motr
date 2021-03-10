package uk.gov.dvsa.motr.notifier.processing.unloader;

import com.amazonaws.services.lambda.runtime.Context;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.events.UnloadingTimedOutEvent;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.queue.QueueItemRemover;
import uk.gov.dvsa.motr.notifier.processing.queue.SubscriptionsReceiver;
import uk.gov.dvsa.motr.notifier.processing.service.ProcessSubscriptionService;

public class QueueUnloader {

    private SubscriptionsReceiver subscriptionsReceiver;
    private QueueItemRemover queueItemRemover;
    private ProcessSubscriptionService processSubscriptionService;
    private int remaingingTimeThresholdMs;

    public QueueUnloader(
            SubscriptionsReceiver subscriptionsReceiver,
            QueueItemRemover queueItemRemover,
            ProcessSubscriptionService processSubscriptionService,
            int remaingingTimeThresholdMs) {

        this.subscriptionsReceiver = subscriptionsReceiver;
        this.queueItemRemover = queueItemRemover;
        this.remaingingTimeThresholdMs = remaingingTimeThresholdMs;
        this.processSubscriptionService = processSubscriptionService;
    }

    public NotifierReport run(Context context) {

        NotifierReport report = new NotifierReport();
        report.startProcessingTheMessages();

        for (SubscriptionQueueItem subscriptionQueueItemFromQueue : subscriptionsReceiver) {

            if (passedTimeoutThreshold(report, context)) {
                break;
            }

            (new ProcessSubscriptionTask(
                processSubscriptionService
            )).run(subscriptionQueueItemFromQueue);
        }

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
