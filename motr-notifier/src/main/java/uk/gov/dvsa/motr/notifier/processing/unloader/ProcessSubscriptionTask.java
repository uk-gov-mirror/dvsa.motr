package uk.gov.dvsa.motr.notifier.processing.unloader;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.events.HgvPsvDetailsRetrievalFailedEvent;
import uk.gov.dvsa.motr.notifier.events.NotifyEvent;
import uk.gov.dvsa.motr.notifier.events.NotifyReminderFailedEvent;
import uk.gov.dvsa.motr.notifier.events.SubscriptionProcessedEvent;
import uk.gov.dvsa.motr.notifier.events.SubscriptionProcessingFailedEvent;
import uk.gov.dvsa.motr.notifier.events.SuccessfulSubscriptionProcessedEvent;
import uk.gov.dvsa.motr.notifier.events.VehicleDetailsRetrievalFailedEvent;
import uk.gov.dvsa.motr.notifier.events.VehicleNotFoundEvent;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.service.ProcessSubscriptionService;
import uk.gov.dvsa.motr.notifier.processing.service.VehicleNotFoundException;
import uk.gov.dvsa.motr.vehicledetails.HgvPsvDetailsClientException;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClientException;
import uk.gov.service.notify.NotificationClientException;

public class ProcessSubscriptionTask {

    private SubscriptionQueueItem subscriptionQueueItemToProcess;
    private ProcessSubscriptionService processSubscriptionService;

    public ProcessSubscriptionTask(ProcessSubscriptionService processSubscriptionService) {

        this.processSubscriptionService = processSubscriptionService;
    }

    public void run(SubscriptionQueueItem subscriptionQueueItemToProcess) throws Exception {

        this.subscriptionQueueItemToProcess = subscriptionQueueItemToProcess;

        Long startedProcessingTime = System.currentTimeMillis();

        try {
            processSubscriptionService.processSubscription(subscriptionQueueItemToProcess);

            EventLogger.logEvent(new SuccessfulSubscriptionProcessedEvent()
                    .setMessageProcessTimeProcessed(System.currentTimeMillis() - startedProcessingTime)
                    .setMessageBody(subscriptionQueueItemToProcess.toString()));
        } catch (VehicleNotFoundException e) {

            SubscriptionProcessedEvent event = populateEvent(new VehicleNotFoundEvent());
            EventLogger.logErrorEvent(event, e);
            throw e;

        } catch (VehicleDetailsClientException e) {

            SubscriptionProcessedEvent event = populateEvent(new VehicleDetailsRetrievalFailedEvent());
            EventLogger.logErrorEvent(event, e);
            throw e;

        } catch (HgvPsvDetailsClientException e) {

            SubscriptionProcessedEvent event = populateEvent(new HgvPsvDetailsRetrievalFailedEvent());
            EventLogger.logErrorEvent(event, e);
            throw e;

        } catch (NotificationClientException e) {

            NotifyEvent event = populateEvent(new NotifyReminderFailedEvent());
            EventLogger.logErrorEvent(event, e);
            throw e;

        } catch (Exception e) {
            EventLogger.logErrorEvent(new SubscriptionProcessingFailedEvent()
                    .setMessageBody(subscriptionQueueItemToProcess.toString())
                    .setMessageProcessTimeProcessed(System.currentTimeMillis() - startedProcessingTime), e);
            throw e;
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
