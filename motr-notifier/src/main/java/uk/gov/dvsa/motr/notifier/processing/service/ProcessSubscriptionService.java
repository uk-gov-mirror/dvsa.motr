package uk.gov.dvsa.motr.notifier.processing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.notifier.events.DeleteSubscriptionSuccessfulEvent;
import uk.gov.dvsa.motr.notifier.events.DvlaIdUpdatedToMotTestNumberEvent;
import uk.gov.dvsa.motr.notifier.events.HgvPsvDetailsRetrievalSuccessfulEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateMotExpiryDateFailedEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateMotExpiryDateSuccessfulEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateVrmFailedEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateVrmSuccessfulEvent;
import uk.gov.dvsa.motr.notifier.notify.NotifyEmailService;
import uk.gov.dvsa.motr.notifier.notify.NotifySmsService;
import uk.gov.dvsa.motr.notifier.processing.factory.SendableNotificationFactory;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.email.SendableEmailNotification;
import uk.gov.dvsa.motr.notifier.processing.model.notification.sms.SendableSmsNotification;
import uk.gov.dvsa.motr.vehicledetails.HgvPsvDetailsClientException;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClientException;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;
import java.util.Optional;

import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motDueDateUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motTestNumberUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.subscriptionDeletionRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.vrmUpdateRequired;

public class ProcessSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessSubscriptionService.class);

    private VehicleDetailsClient client;
    private SubscriptionRepository subscriptionRepository;
    private NotifyEmailService notifyEmailService;
    private NotifySmsService notifySmsService;
    private SendableNotificationFactory notificationFactory;
    private Boolean hgvPsvNotifications;

    public ProcessSubscriptionService(
            VehicleDetailsClient client,
            SubscriptionRepository repository,
            NotifyEmailService notifyEmailService,
            NotifySmsService notifySmsService,
            SendableNotificationFactory notificationFactory,
            Boolean hgvPsvNotifications) {

        this.client = client;
        this.subscriptionRepository = repository;
        this.notifyEmailService = notifyEmailService;
        this.notifySmsService = notifySmsService;
        this.notificationFactory = notificationFactory;
        this.hgvPsvNotifications = hgvPsvNotifications;
    }

    public void processSubscription(SubscriptionQueueItem subscription) throws NotificationClientException,
            VehicleDetailsClientException, VehicleNotFoundException, HgvPsvDetailsClientException {

        VehicleDetails vehicleDetails = getVehicleDetails(subscription);

        String vrm = subscription.getVrm();
        String email = subscription.getContactDetail().getValue();
        LocalDate subscriptionMotDueDate = subscription.getMotDueDate();
        String subscriptionMotTestNumber = subscription.getMotTestNumber();
        String subscriptionDvlaId = subscription.getDvlaId();
        LocalDate requestDate = subscription.getLoadedOnDate();

        LocalDate vehicleMotExpiryDate = vehicleDetails.getMotExpiryDate();
        String vehicleMotTestNumber = vehicleDetails.getMotTestNumber();
        String vehicleVrm = vehicleDetails.getRegNumber();

        VehicleType vehicleType = subscription.getVehicleType();

        if (subscriptionDeletionRequired(vehicleMotExpiryDate, requestDate)) {
            subscriptionRepository.deleteSubscription(vrm, email);
            EventLogger.logEvent(new DeleteSubscriptionSuccessfulEvent().setVrm(vrm).setMotExpiryDate(vehicleMotExpiryDate));
            return;
        }

        if (motDueDateUpdateRequired(subscriptionMotDueDate, vehicleMotExpiryDate)) {
            try {
                subscriptionRepository.updateExpiryDate(vrm, email, vehicleMotExpiryDate);
                EventLogger.logEvent(new UpdateMotExpiryDateSuccessfulEvent()
                        .setNewExpiryDate(vehicleMotExpiryDate)
                        .setExpiryDate(subscriptionMotDueDate)
                        .setEmail(email)
                        .setVrm(vrm));
            } catch (Exception e) {
                EventLogger.logErrorEvent(new UpdateMotExpiryDateFailedEvent()
                        .setNewExpiryDate(vehicleMotExpiryDate)
                        .setExpiryDate(subscriptionMotDueDate)
                        .setEmail(email)
                        .setVrm(vrm), e);
            }
        }

        if (!this.hgvPsvNotifications && VehicleType.isCommercialVehicle(vehicleType)) {
            logger.debug("HGV/PSV notifications not enabled {}", vrm);
        } else {
            SubscriptionQueueItem.ContactType contactType = subscription.getContactDetail().getContactType();
            if (contactType == SubscriptionQueueItem.ContactType.EMAIL) {
                sendEmailNotfication(subscription, requestDate, vehicleDetails);
            } else if (contactType == SubscriptionQueueItem.ContactType.MOBILE) {
                sendSmsNotification(subscription, vehicleDetails);
            }
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

    private void sendEmailNotfication(SubscriptionQueueItem subscription, LocalDate requestDate, VehicleDetails vehicleDetails)
            throws NotificationClientException {

        String email = subscription.getContactDetail().getValue();

        Optional<SendableEmailNotification> notification = this.notificationFactory.getEmailNotification(
                requestDate, subscription, vehicleDetails);

        if (notification.isPresent()) {
            notifyEmailService.sendEmail(email, notification.get(), vehicleDetails);
        }
    }

    private void sendSmsNotification(SubscriptionQueueItem subscription, VehicleDetails vehicleDetails)
            throws NotificationClientException {

        String phoneNumber = subscription.getContactDetail().getValue();

        Optional<SendableSmsNotification> notification = this.notificationFactory.getSmsNotification(
                subscription, vehicleDetails);

        if (notification.isPresent()) {
            notifySmsService.sendSms(phoneNumber, notification.get());
        }
    }

    private VehicleDetails getVehicleDetails(SubscriptionQueueItem subscriptionQueueItem) throws VehicleDetailsClientException,
            VehicleNotFoundException, HgvPsvDetailsClientException {

        final String dvlaId = subscriptionQueueItem.getDvlaId();
        final String motTestNumber = subscriptionQueueItem.getMotTestNumber();
        final String vrm = subscriptionQueueItem.getVrm();
        final VehicleType vehicleType = subscriptionQueueItem.getVehicleType();

        logger.debug("Subscription dvlaId is {}, mot test number is {} and vrm is {}", dvlaId, motTestNumber, vrm);

        if (vehicleType == VehicleType.MOT && motTestNumber != null) {
            logger.trace("going to fetch by mot test number");

            return client.fetchByMotTestNumber(motTestNumber).orElseThrow(() -> {
                logger.debug("no vehicle found for mot_test_number {}", motTestNumber);
                return new VehicleNotFoundException("no vehicle found for mot_test_number: " + motTestNumber);
            });
        }

        if (VehicleType.isCommercialVehicle(vehicleType) && vrm != null) {
            logger.trace("going to fetch HGV/PSV/trailer data by vrm");

            VehicleDetails vehicleDetails = client.fetchHgvPsvByVrm(vrm).orElseThrow(() -> {
                logger.debug("no HGV/PSV/trailer vehicle found for vrm {}", vrm);
                return new VehicleNotFoundException("no HGV/PSV vehicle found for vrm " + vrm);
            });
            EventLogger.logEvent(new HgvPsvDetailsRetrievalSuccessfulEvent());
            return vehicleDetails;
        }

        if (dvlaId != null) {
            logger.trace("going to fetch by dvla id");

            return client.fetchByDvlaId(dvlaId).orElseThrow(() -> {
                logger.debug("no vehicle found for dvla id {}", dvlaId);
                return new VehicleNotFoundException("no vehicle found for dvlaid: " + dvlaId);
            });
        }

        logger.debug("no attribute to search for subscription {}", subscriptionQueueItem);
        throw new VehicleNotFoundException("no data to search for subscription " + subscriptionQueueItem.getId());
    }
}
