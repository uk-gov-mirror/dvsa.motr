package uk.gov.dvsa.motr.notifier.processing.unloader;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.events.NotifyReminderFailedEvent;
import uk.gov.dvsa.motr.notifier.events.SubscriptionProcessedEvent;
import uk.gov.dvsa.motr.notifier.events.SubscriptionProcessingFailedEvent;
import uk.gov.dvsa.motr.notifier.events.SubscriptionQueueItemRemovalFailedEvent;
import uk.gov.dvsa.motr.notifier.events.SuccessfulSubscriptionProcessedEvent;
import uk.gov.dvsa.motr.notifier.events.VehicleDetailsRetrievalFailedEvent;
import uk.gov.dvsa.motr.notifier.events.VehicleNotFoundEvent;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.queue.QueueItemRemover;
import uk.gov.dvsa.motr.notifier.processing.queue.RemoveSubscriptionFromQueueException;
import uk.gov.dvsa.motr.notifier.processing.service.ProcessSubscriptionService;
import uk.gov.dvsa.motr.notifier.processing.service.VehicleNotFoundException;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClientException;
import uk.gov.service.notify.NotificationClientException;

public class ProcessSubscriptionTask implements Runnable {

    private SubscriptionQueueItem subscriptionQueueItemToProcess;
    private NotifierReport report;
    private ProcessSubscriptionService processSubscriptionService;
    private QueueItemRemover queueItemRemover;

    public ProcessSubscriptionTask(
            SubscriptionQueueItem subscriptionQueueItemToProcess,
            NotifierReport report,
            ProcessSubscriptionService processSubscriptionService,
            QueueItemRemover queueItemRemover) {

        this.subscriptionQueueItemToProcess = subscriptionQueueItemToProcess;
        this.report = report;
        this.processSubscriptionService = processSubscriptionService;
        this.queueItemRemover = queueItemRemover;
    }

    @Override
    public void run() {

        Long startedProcessingTime = System.currentTimeMillis();

        try {

            processSubscriptionService.processSubscription(subscriptionQueueItemToProcess);

            queueItemRemover.removeProcessedQueueItem(subscriptionQueueItemToProcess);

            EventLogger.logEvent(new SuccessfulSubscriptionProcessedEvent()
                    .setMessageProcessTimeProcessed(System.currentTimeMillis() - startedProcessingTime)
                    .setMessageBody(subscriptionQueueItemToProcess.toString()));

            report.incrementSuccessfullyProcessed();

        } catch (RemoveSubscriptionFromQueueException e) {

            SubscriptionProcessedEvent event = populateEvent(new SubscriptionQueueItemRemovalFailedEvent());
            EventLogger.logErrorEvent(event, e);
            report.incrementFailedToProcess();

        } catch (VehicleNotFoundException e) {

            SubscriptionProcessedEvent event = populateEvent(new VehicleNotFoundEvent());
            EventLogger.logErrorEvent(event, e);
            report.incrementFailedToProcess();

        } catch (VehicleDetailsClientException e) {

            SubscriptionProcessedEvent event = populateEvent(new VehicleDetailsRetrievalFailedEvent());
            EventLogger.logErrorEvent(event, e);
            report.incrementFailedToProcess();

        } catch (NotificationClientException e) {

            NotifyEvent event = populateEvent(new NotifyReminderFailedEvent());
            EventLogger.logErrorEvent(event, e);
            report.incrementFailedToProcess();

        } catch (Exception e) {
            EventLogger.logErrorEvent(new SubscriptionProcessingFailedEvent()
                    .setMessageBody(subscriptionQueueItemToProcess.toString())
                    .setMessageProcessTimeProcessed(System.currentTimeMillis() - startedProcessingTime), e);

            report.incrementFailedToProcess();
        }
    }

    private NotifyEvent populateEvent(NotifyEvent event) {
        event.setEmail(subscriptionQueueItemToProcess.getContactDetail().getValue())
                .setContactType(subscriptionQueueItemToProcess.getContactDetail().getContactType())
                .setVrm(subscriptionQueueItemToProcess.getVrm())
                .setExpiryDate(subscriptionQueueItemToProcess.getMotDueDate());

        if (subscriptionQueueItemToProcess.getMotTestNumber() == null) {
            event.setDvlaId(subscriptionQueueItemToProcess.getDvlaId());
        } else {
            event.setMotTestNumber(subscriptionQueueItemToProcess.getMotTestNumber());
        }
        return event;
    }

    private SubscriptionProcessedEvent populateEvent(SubscriptionProcessedEvent event) {
        event.setEmail(subscriptionQueueItemToProcess.getContactDetail().getValue())
                .setVrm(subscriptionQueueItemToProcess.getVrm())
                .setExpiryDate(subscriptionQueueItemToProcess.getMotDueDate());

        if (subscriptionQueueItemToProcess.getMotTestNumber() == null) {
            event.setDvlaId(subscriptionQueueItemToProcess.getDvlaId());
        } else {
            event.setMotTestNumber(subscriptionQueueItemToProcess.getMotTestNumber());
        }
        return event;
    }
}
