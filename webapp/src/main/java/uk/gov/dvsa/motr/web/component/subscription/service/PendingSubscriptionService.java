package uk.gov.dvsa.motr.web.component.subscription.service;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.remote.vehicledetails.MotIdentification;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsService;
import uk.gov.dvsa.motr.web.component.subscription.helper.UrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.ContactDetail;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.response.PendingSubscriptionServiceResponse;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreatedEvent;
import uk.gov.dvsa.motr.web.eventlog.subscription.PendingSubscriptionCreationFailedEvent;
import uk.gov.dvsa.motr.web.formatting.MakeModelFormatter;

import java.time.LocalDate;
import java.util.Optional;

import javax.inject.Inject;

import static uk.gov.dvsa.motr.web.component.subscription.service.RandomIdGenerator.generateId;

public class PendingSubscriptionService {

    private PendingSubscriptionRepository pendingSubscriptionRepository;
    private SubscriptionRepository subscriptionRepository;
    private NotifyService notifyService;
    private UrlHelper urlHelper;
    private VehicleDetailsClient client;

    @Inject
    public PendingSubscriptionService(
            PendingSubscriptionRepository pendingSubscriptionRepository,
            SubscriptionRepository subscriptionRepository,
            NotifyService notifyService,
            UrlHelper urlHelper,
            VehicleDetailsClient client
    ) {

        this.pendingSubscriptionRepository = pendingSubscriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notifyService = notifyService;
        this.urlHelper = urlHelper;
        this.client = client;
    }

    public PendingSubscriptionServiceResponse handlePendingSubscriptionCreation(
            String vrm,
            ContactDetail contactDetail,
            LocalDate motDueDate,
            MotIdentification motIdentification) {

        String contact = contactDetail.getValue();
        Subscription.ContactType contactType = contactDetail.getContactType();

        Optional<Subscription> subscription = subscriptionRepository.findByVrmAndEmail(vrm, contact);
        PendingSubscriptionServiceResponse pendingSubscriptionResponse = new PendingSubscriptionServiceResponse();

        if (subscription.isPresent()) {
            return getRedirectUrlWhenSubscriptionAlreadyExisis(subscription.get(), motDueDate, contactType, pendingSubscriptionResponse);
        } else {
            return getRedirectUrlWhenNewSubscription(vrm, contact, motDueDate, motIdentification,
                    contactType, pendingSubscriptionResponse);
        }
    }

    private PendingSubscriptionServiceResponse getRedirectUrlWhenSubscriptionAlreadyExisis(
            Subscription subscription, LocalDate motDueDate,
            Subscription.ContactType contactType,
            PendingSubscriptionServiceResponse pendingSubscriptionResponse) {

        updateSubscriptionMotDueDate(subscription, motDueDate);

        String redirectUri = (contactType == Subscription.ContactType.EMAIL
                ? urlHelper.emailConfirmedNthTimeLink() : urlHelper.phoneConfirmedNthTimeLink());

        return pendingSubscriptionResponse.setRedirectUri(redirectUri);
    }

    private PendingSubscriptionServiceResponse getRedirectUrlWhenNewSubscription(String vrm, String contact, LocalDate motDueDate,
            MotIdentification motIdentification, Subscription.ContactType contactType,
            PendingSubscriptionServiceResponse pendingSubscriptionResponse) {

        String confimrationId;
        Optional<PendingSubscription> pendingSubscription = pendingSubscriptionRepository.findByVrmAndContactDetails(vrm, contact);
        if (pendingSubscription.isPresent() && contactType == Subscription.ContactType.MOBILE) {
            confimrationId = pendingSubscription.get().getConfirmationId();
        } else {
            confimrationId = generateId();
            createPendingSubscription(vrm, contact, motDueDate, confimrationId, motIdentification, contactType);
        }

        return contactType == Subscription.ContactType.EMAIL
                ? pendingSubscriptionResponse.setRedirectUri(urlHelper.emailConfirmationPendingLink())
                : pendingSubscriptionResponse.setConfirmationId(confimrationId);
    }

    /**
     * Creates pending subscription in the system to be confirmed later by confirmation link
     * @param vrm        subscription vrm
     * @param contact      subscription contact
     * @param motDueDate most recent mot due date
     * @param confirmationId confirmation id
     * @param motIdentification the identifier for this vehicle (may be dvla id or mot test number)
     */
    public void createPendingSubscription(
            String vrm, String contact, LocalDate motDueDate,
            String confirmationId, MotIdentification motIdentification,
            Subscription.ContactType contactType) {

        PendingSubscription pendingSubscription = new PendingSubscription()
                .setConfirmationId(confirmationId)
                .setContactDetail(new ContactDetail(contact, contactType))
                .setVrm(vrm)
                .setMotDueDate(motDueDate)
                .setMotIdentification(motIdentification);

        try {
            pendingSubscriptionRepository.save(pendingSubscription);

            if (contactType == Subscription.ContactType.EMAIL) {

                VehicleDetails vehicleDetails = VehicleDetailsService.getVehicleDetails(vrm, client);
                notifyService.sendEmailAddressConfirmationEmail(
                        contact,
                        urlHelper.confirmSubscriptionLink(pendingSubscription.getConfirmationId()),
                        MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails, ", ") + vrm
                );
            }
            EventLogger.logEvent(
                    new PendingSubscriptionCreatedEvent().setVrm(vrm).setEmail(contact).setMotDueDate(motDueDate)
                    .setMotIdentification(motIdentification)
            );
        } catch (Exception e) {
            EventLogger.logErrorEvent(
                    new PendingSubscriptionCreationFailedEvent().setVrm(vrm).setEmail(contact).setMotDueDate(motDueDate)
                    .setMotIdentification(motIdentification), e);
            throw e;
        }
    }

    private Subscription updateSubscriptionMotDueDate(Subscription subscription, LocalDate motDueDate) {

        subscription.setMotDueDate(motDueDate);
        subscriptionRepository.save(subscription);

        return subscription;
    }
}
