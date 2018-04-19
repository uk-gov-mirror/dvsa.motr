package uk.gov.dvsa.motr.notifier.processing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.notifier.events.DeleteSubscriptionSuccessfulEvent;
import uk.gov.dvsa.motr.notifier.events.DvlaIdUpdatedToMotTestNumberEvent;
import uk.gov.dvsa.motr.notifier.events.OneDayAfterEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.events.OneDayAfterSmsReminderEvent;
import uk.gov.dvsa.motr.notifier.events.OneMonthEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.events.OneMonthSmsReminderEvent;
import uk.gov.dvsa.motr.notifier.events.TwoWeekEmailReminderEvent;
import uk.gov.dvsa.motr.notifier.events.TwoWeekSmsReminderEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateMotExpiryDateFailedEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateMotExpiryDateSuccessfulEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateVrmFailedEvent;
import uk.gov.dvsa.motr.notifier.events.UpdateVrmSuccessfulEvent;
import uk.gov.dvsa.motr.notifier.helpers.Checksum;
import uk.gov.dvsa.motr.notifier.notify.NotifyEmailService;
import uk.gov.dvsa.motr.notifier.notify.NotifySmsService;
import uk.gov.dvsa.motr.notifier.processing.formatting.MakeModelFormatter;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetailsClientException;
import uk.gov.service.notify.NotificationClientException;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.StringJoiner;

import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motDueDateUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motTestNumberUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.oneDayAfterNotificationRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.oneMonthNotificationRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.subscriptionDeletionRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.twoWeekNotificationRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.vrmUpdateRequired;

public class ProcessSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessSubscriptionService.class);

    private VehicleDetailsClient client;
    private SubscriptionRepository subscriptionRepository;
    private NotifyEmailService notifyEmailService;
    private NotifySmsService notifySmsService;
    private String webBaseUrl;
    private String mothDirectUrlPrefix;
    private String checksumSalt;

    public ProcessSubscriptionService(
            VehicleDetailsClient client,
            SubscriptionRepository repository,
            NotifyEmailService notifyEmailService,
            NotifySmsService notifySmsService,
            String webBaseUrl,
            String mothDirectUrlPrefix,
            String checksumSalt) {

        this.client = client;
        this.subscriptionRepository = repository;
        this.notifyEmailService = notifyEmailService;
        this.notifySmsService = notifySmsService;
        this.webBaseUrl = webBaseUrl;
        this.mothDirectUrlPrefix = mothDirectUrlPrefix;
        this.checksumSalt = checksumSalt;
    }

    public void processSubscription(SubscriptionQueueItem subscriptionQueueItem, LocalDate requestDate) throws NotificationClientException,
            VehicleDetailsClientException, VehicleNotFoundException, NoSuchAlgorithmException {

        VehicleDetails vehicleDetails = getVehicleDetails(subscriptionQueueItem);

        String vrm = subscriptionQueueItem.getVrm();
        String email = subscriptionQueueItem.getContactDetail().getValue();
        LocalDate subscriptionMotDueDate = subscriptionQueueItem.getMotDueDate();
        String subscriptionMotTestNumber = subscriptionQueueItem.getMotTestNumber();
        String subscriptionDvlaId = subscriptionQueueItem.getDvlaId();

        LocalDate vehicleMotExpiryDate = vehicleDetails.getMotExpiryDate();
        String vehicleMotTestNumber = vehicleDetails.getMotTestNumber();
        String vehicleVrm = vehicleDetails.getRegNumber();

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

        SubscriptionQueueItem.ContactType contactType = subscriptionQueueItem.getContactDetail().getContactType();
        if (contactType == SubscriptionQueueItem.ContactType.EMAIL) {
            sendEmailNotfications(subscriptionQueueItem, requestDate);
        } else if (contactType == SubscriptionQueueItem.ContactType.MOBILE) {
            sendSmsNotifications(subscriptionQueueItem, requestDate);
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

    private void sendEmailNotfications(SubscriptionQueueItem subscriptionQueueItem, LocalDate requestDate)
            throws NotificationClientException, VehicleDetailsClientException, VehicleNotFoundException, NoSuchAlgorithmException {

        VehicleDetails vehicleDetails = getVehicleDetails(subscriptionQueueItem);
        String email = subscriptionQueueItem.getContactDetail().getValue();
        String subscriptionId = subscriptionQueueItem.getId();
        LocalDate vehicleMotExpiryDate = vehicleDetails.getMotExpiryDate();
        String vrm = vehicleDetails.getRegNumber();

        String vehicleDetailsString = MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails, ", ") + vrm;
        String unSubscribeLink = UriBuilder.fromPath(webBaseUrl).path("unsubscribe").path(subscriptionId).build().toString();

        if (oneMonthNotificationRequired(requestDate, vehicleMotExpiryDate)) {

            String checksum = Checksum.generate(vehicleDetails, this.checksumSalt);
            StringJoiner mothDirectUrl = new StringJoiner("");
            mothDirectUrl.add(this.mothDirectUrlPrefix);
            mothDirectUrl.add(vehicleDetails.getRegNumber());
            mothDirectUrl.add("/");
            mothDirectUrl.add(checksum);

            notifyEmailService.sendOneMonthNotificationEmail(
                    email,
                    vehicleDetailsString,
                    vehicleMotExpiryDate,
                    unSubscribeLink,
                    vehicleDetails.getMotTestNumber(),
                    mothDirectUrl.toString()
            );

            EventLogger.logEvent(new OneMonthEmailReminderEvent()
                    .setChecksum(checksum)
                    .setEmail(email)
                    .setVrm(vrm)
                    .setExpiryDate(vehicleMotExpiryDate));
        }

        if (twoWeekNotificationRequired(requestDate, vehicleMotExpiryDate)) {
            notifyEmailService.sendTwoWeekNotificationEmail(
                    email,
                    vehicleDetailsString,
                    vehicleMotExpiryDate,
                    unSubscribeLink,
                    vehicleDetails.getMotTestNumber()
            );

            EventLogger.logEvent(new TwoWeekEmailReminderEvent()
                    .setEmail(email)
                    .setVrm(vrm)
                    .setExpiryDate(vehicleMotExpiryDate));
        }

        if (oneDayAfterNotificationRequired(requestDate, vehicleMotExpiryDate)) {
            notifyEmailService.sendOneDayAfterNotificationEmail(
                    email,
                    vehicleDetailsString,
                    vehicleMotExpiryDate,
                    unSubscribeLink,
                    vehicleDetails.getMotTestNumber()
            );

            EventLogger.logEvent(new OneDayAfterEmailReminderEvent()
                    .setEmail(email)
                    .setVrm(vrm)
                    .setExpiryDate(vehicleMotExpiryDate));
        }
    }

    private void sendSmsNotifications(SubscriptionQueueItem subscriptionQueueItem, LocalDate requestDate)
            throws NotificationClientException, VehicleDetailsClientException, VehicleNotFoundException {

        VehicleDetails vehicleDetails = getVehicleDetails(subscriptionQueueItem);
        String vrm = vehicleDetails.getRegNumber();
        LocalDate vehicleMotExpiryDate = vehicleDetails.getMotExpiryDate();
        String phoneNumber = subscriptionQueueItem.getContactDetail().getValue();

        if (oneMonthNotificationRequired(requestDate, vehicleMotExpiryDate)) {

            notifySmsService.sendOneMonthNotificationSms(phoneNumber, vrm, vehicleMotExpiryDate);
            EventLogger.logEvent(new OneMonthSmsReminderEvent()
                    .setEmail(phoneNumber)
                    .setVrm(vrm)
                    .setExpiryDate(vehicleMotExpiryDate));
        }

        if (twoWeekNotificationRequired(requestDate, vehicleMotExpiryDate)) {

            notifySmsService.sendTwoWeekNotificationSms(phoneNumber, vrm, vehicleMotExpiryDate);
            EventLogger.logEvent(new TwoWeekSmsReminderEvent()
                    .setEmail(phoneNumber)
                    .setVrm(vrm)
                    .setExpiryDate(vehicleMotExpiryDate));
        }

        if (oneDayAfterNotificationRequired(requestDate, vehicleMotExpiryDate)) {

            notifySmsService.sendOneDayAfterNotificationSms(phoneNumber, vrm);
            EventLogger.logEvent(new OneDayAfterSmsReminderEvent()
                    .setEmail(phoneNumber)
                    .setVrm(vrm)
                    .setExpiryDate(vehicleMotExpiryDate));
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
