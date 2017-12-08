package uk.gov.dvsa.motr.notifier.processing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.notifier.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.notifier.notify.NotifyService;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetails;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClient;
import uk.gov.dvsa.motr.remote.vehicledetails.VehicleDetailsClientException;
import uk.gov.service.notify.NotificationClientException;

import java.time.LocalDate;

import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.motDueDateUpdateRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.oneMonthEmailRequired;
import static uk.gov.dvsa.motr.notifier.processing.service.SubscriptionHandlerHelper.twoWeekEmailRequired;

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

        String vrm = subscriptionQueueItem.getVrm();

        VehicleDetails vehicleDetails = client.fetch(vrm).orElseThrow(() -> {
            logger.debug("no vehicle found for vrm {}", vrm);
            return new VehicleNotFoundException("no vehicle found for vrm: " + subscriptionQueueItem.getVrm());
        });

        String email = subscriptionQueueItem.getEmail();
        String subscriptionId = subscriptionQueueItem.getId();
        LocalDate subscriptionMotDueDate = subscriptionQueueItem.getMotDueDate();
        LocalDate vehicleMotExpiryDate = vehicleDetails.getMotExpiryDate();
        String unSubscribeLink = UriBuilder.fromPath(webBaseUrl).path("unsubscribe").path(subscriptionId).build().toString();

        if (motDueDateUpdateRequired(subscriptionMotDueDate, vehicleMotExpiryDate)) {
            subscriptionRepository.updateExpiryDate(vrm, email, vehicleMotExpiryDate);
        }

        if (oneMonthEmailRequired(requestDate, vehicleMotExpiryDate)) {
            notifyService.sendOneMonthNotificationEmail(email, vrm, vehicleMotExpiryDate, unSubscribeLink);
        }

        if (twoWeekEmailRequired(requestDate, vehicleMotExpiryDate)) {
            notifyService.sendTwoWeekNotificationEmail(email, vrm, vehicleMotExpiryDate, unSubscribeLink);
        }
    }
}
