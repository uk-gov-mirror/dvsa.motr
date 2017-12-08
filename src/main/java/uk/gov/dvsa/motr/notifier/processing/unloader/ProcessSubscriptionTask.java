package uk.gov.dvsa.motr.notifier.processing.unloader;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.events.NotifyReminderFailedEvent;
import uk.gov.dvsa.motr.notifier.events.SubscriptionProcessingFailedEvent;
import uk.gov.dvsa.motr.notifier.events.SubscriptionQueueItemRemovalFailedEvent;
import uk.gov.dvsa.motr.notifier.events.SuccessfulSubscriptionProcessedEvent;
import uk.gov.dvsa.motr.notifier.events.VehicleDetailsRetrievalFailedEvent;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.queue.QueueItemRemover;
import uk.gov.dvsa.motr.notifier.processing.queue.RemoveSubscriptionFromQueueException;
import uk.gov.dvsa.motr.notifier.processing.service.ProcessSubscriptionService;
import uk.gov.dvsa.motr.notifier.processing.service.VehicleNotFoundException;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;

public class ProcessSubscriptionTask implements Runnable {

    private LocalDate requestDate;
    private SubscriptionQueueItem subscriptionQueueItemToProcess;
    private NotifierReport report;
    private ProcessSubscriptionService processSubscriptionService;
    private QueueItemRemover queueItemRemover;

    public ProcessSubscriptionTask(
            LocalDate requestDate,
            SubscriptionQueueItem subscriptionQueueItemToProcess,
            NotifierReport report,
            ProcessSubscriptionService processSubscriptionService,
            QueueItemRemover queueItemRemover) {

        this.requestDate = requestDate;
        this.subscriptionQueueItemToProcess = subscriptionQueueItemToProcess;
        this.report = report;
        this.processSubscriptionService = processSubscriptionService;
        this.queueItemRemover = queueItemRemover;
    }

    @Override
    public void run() {

        Long startedProcessingTime = System.currentTimeMillis();

        try {

            processSubscriptionService.processSubscription(subscriptionQueueItemToProcess, requestDate);

            queueItemRemover.removeProcessedQueueItem(subscriptionQueueItemToProcess);

            EventLogger.logEvent(new SuccessfulSubscriptionProcessedEvent()
                    .setMessageProcessTimeProcessed(System.currentTimeMillis() - startedProcessingTime)
                    .setMessageBody(subscriptionQueueItemToProcess.toString()));

            report.incrementSuccessfullyProcessed();

        } catch (RemoveSubscriptionFromQueueException e) {
            EventLogger.logErrorEvent(new SubscriptionQueueItemRemovalFailedEvent()
                    .setEmail(subscriptionQueueItemToProcess.getEmail())
                    .setVrm(subscriptionQueueItemToProcess.getVrm())
                    .setMotTestNumber(subscriptionQueueItemToProcess.getMotTestNumber())
                    .setExpiryDate(subscriptionQueueItemToProcess.getMotDueDate()), e);

            report.incrementFailedToProcess();

        } catch (VehicleDetailsClientException | VehicleNotFoundException e) {
            EventLogger.logErrorEvent(new VehicleDetailsRetrievalFailedEvent()
                    .setEmail(subscriptionQueueItemToProcess.getEmail())
                    .setVrm(subscriptionQueueItemToProcess.getVrm())
                    .setMotTestNumber(subscriptionQueueItemToProcess.getMotTestNumber())
                    .setExpiryDate(subscriptionQueueItemToProcess.getMotDueDate()), e);

            report.incrementFailedToProcess();

        } catch (NotificationClientException e) {
            EventLogger.logErrorEvent(new NotifyReminderFailedEvent()
                    .setEmail(subscriptionQueueItemToProcess.getEmail())
                    .setVrm(subscriptionQueueItemToProcess.getVrm())
                    .setMotTestNumber(subscriptionQueueItemToProcess.getMotTestNumber())
                    .setExpiryDate(subscriptionQueueItemToProcess.getMotDueDate()), e);

            report.incrementFailedToProcess();

        } catch (Exception e) {
            EventLogger.logErrorEvent(new SubscriptionProcessingFailedEvent()
                    .setMessageBody(subscriptionQueueItemToProcess.toString())
                    .setMessageProcessTimeProcessed(System.currentTimeMillis() - startedProcessingTime), e);

            report.incrementFailedToProcess();
        }
    }
}
