package uk.gov.dvsa.motr.notifier.processing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.notifier.events.DeleteSubscriptionSuccessfulEvent;
import uk.gov.dvsa.motr.notifier.events.DvlaIdUpdatedToMotTestNumberEvent;
import uk.gov.dvsa.motr.notifier.events.OneDayAfterEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.events.OneMonthEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.events.TwoWeekEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateVrmFailedEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateVrmSuccessfulEvent;
import uk.gov.dvsa.motr.notifier.notify.NotifyService;
import uk.gov.dvsa.motr.notifier.processing.formatting.MakeModelFormatter;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;

import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motDueDateUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motTestNumberUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.oneDayAfterEmailRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.oneMonthEmailRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.subscriptionDeletionRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.twoWeekEmailRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.vrmUpdateRequired;

public class ProcessSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessSubscriptionService.class);

    private VehicleDetailsClient client;
    private SubscriptionRepository subscriptionRepository;
    private NotifyService notifyService;
    private String webBaseUrl;

    public ProcessSubscriptionService(
            VehicleDetailsClient client,
            SubscriptionRepository repository,
            NotifyService notifyService,
            String webBaseUrl) {

        this.client = client;
        this.subscriptionRepository = repository;
        this.notifyService = notifyService;
        this.webBaseUrl = webBaseUrl;
    }

    public void processSubscription(SubscriptionQueueItem subscriptionQueueItem, LocalDate requestDate) throws NotificationClientException,
            VehicleDetailsClientException, VehicleNotFoundException {

        VehicleDetails vehicleDetails = getVehicleDetails(subscriptionQueueItem);

        String vrm = subscriptionQueueItem.getVrm();
        String email = subscriptionQueueItem.getEmail();
        String subscriptionId = subscriptionQueueItem.getId();
        LocalDate subscriptionMotDueDate = subscriptionQueueItem.getMotDueDate();
        String subscriptionMotTestNumber = subscriptionQueueItem.getMotTestNumber();
        String subscriptionDvlaId = subscriptionQueueItem.getDvlaId();

        LocalDate vehicleMotExpiryDate = vehicleDetails.getMotExpiryDate();
        String vehicleMotTestNumber = vehicleDetails.getMotTestNumber();
        String vehicleVrm = vehicleDetails.getRegNumber();

        String unSubscribeLink = UriBuilder.fromPath(webBaseUrl).path("unsubscribe").path(subscriptionId).build().toString();

        if (subscriptionDeletionRequired(vehicleMotExpiryDate, requestDate)) {
            subscriptionRepository.deleteSubscription(vrm, email);
            EventLogger.logEvent(new DeleteSubscriptionSuccessfulEvent().setVrm(vrm).setMotExpiryDate(vehicleMotExpiryDate));
            return;
        }

        if (motDueDateUpdateRequired(subscriptionMotDueDate, vehicleMotExpiryDate)) {
            subscriptionRepository.updateExpiryDate(vrm, email, vehicleMotExpiryDate);
        }

        String vehicleDetailsString = MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails, ", ") + vehicleVrm;

        if (oneMonthEmailRequired(requestDate, vehicleMotExpiryDate)) {

            notifyService.sendOneMonthNotificationEmail(
                    email,
                    vehicleDetailsString,
                    vehicleMotExpiryDate,
                    unSubscribeLink,
                    vehicleDetails.getDvlaId()
            );

            EventLogger.logEvent(new OneMonthEmailReminderEvent()
                    .setEmail(email)
                    .setVrm(vehicleVrm)
                    .setExpiryDate(vehicleMotExpiryDate));
        }

        if (twoWeekEmailRequired(requestDate, vehicleMotExpiryDate)) {
            notifyService.sendTwoWeekNotificationEmail(
                    email,
                    vehicleDetailsString,
                    vehicleMotExpiryDate,
                    unSubscribeLink,
                    vehicleDetails.getDvlaId()
            );

            EventLogger.logEvent(new TwoWeekEmailReminderEvent()
                    .setEmail(email)
                    .setVrm(vehicleVrm)
                    .setExpiryDate(vehicleMotExpiryDate));
        }

        if (oneDayAfterEmailRequired(requestDate, vehicleMotExpiryDate)) {
            notifyService.sendOneDayAfterNotificationEmail(
                    email,
                    vehicleDetailsString,
                    vehicleMotExpiryDate,
                    unSubscribeLink,
                    vehicleDetails.getDvlaId()
            );

            EventLogger.logEvent(new OneDayAfterEmailReminderEvent()
                    .setEmail(email)
                    .setVrm(vehicleVrm)
                    .setExpiryDate(vehicleMotExpiryDate));
        }

        if (motTestNumberUpdateRequired(subscriptionMotTestNumber, vehicleMotTestNumber)) {
            subscriptionRepository.updateMotTestNumber(vrm, email, vehicleMotTestNumber);

            if (subscriptionDvlaId != null) {
                EventLogger.logEvent(new DvlaIdUpdatedToMotTestNumberEvent()
                        .setExistingDvlaId(subscriptionDvlaId)
                        .setNewMotTestNumber(vehicleMotTestNumber));
            }
        }

        if (vrmUpdateRequired(vrm, vehicleVrm)) {
            try {
                subscriptionRepository.updateVrm(vrm, email, vehicleVrm);
                EventLogger.logEvent(new UpdateVrmSuccessfulEvent().setExistingVrm(vrm).setNewVrm(vehicleVrm));
            } catch (Exception e) {
                EventLogger.logErrorEvent(new UpdateVrmFailedEvent().setExistingVrm(vrm).setNewVrm(vehicleVrm), e);
            }
        }
    }

    private VehicleDetails getVehicleDetails(SubscriptionQueueItem subscriptionQueueItem) throws VehicleDetailsClientException,
            VehicleNotFoundException {

        String subscriptionDvlaId = subscriptionQueueItem.getDvlaId();
        String subscriptionMotTestNumber = subscriptionQueueItem.getMotTestNumber();

        logger.debug("Subscription dvlaId is {}, and subscription mot test number is {}", subscriptionDvlaId, subscriptionMotTestNumber);

        if (subscriptionMotTestNumber == null && subscriptionDvlaId != null) {

            logger.trace("going to fetch by dvla id");

            return client.fetchByDvlaId(subscriptionDvlaId).orElseThrow(() -> {
                logger.debug("no vehicle found for dvla id {}", subscriptionDvlaId);
                return new VehicleNotFoundException("no vehicle found for dvlaid: " + subscriptionDvlaId);
            });
        }

        logger.trace("going to fetch by mot test number");

        return client.fetchByMotTestNumber(subscriptionMotTestNumber).orElseThrow(() -> {
            logger.debug("no vehicle found for mot_test_number {}", subscriptionMotTestNumber);
            return new VehicleNotFoundException("no vehicle found for mot_test_number: " + subscriptionMotTestNumber);
        });
    }
}
