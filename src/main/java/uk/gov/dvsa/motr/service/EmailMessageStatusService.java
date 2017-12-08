package uk.gov.dvsa.motr.service;

import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationList;

import java.util.ArrayList;
import java.util.List;

public class EmailMessageStatusService {

    private static final String EMAIL_MESSAGE_TYPE = "email";
    private static final String PERMANENT_FAILURE_MESSAGE_STATUS = "permanent-failure";

    private NotificationClient notificationClient;

    public EmailMessageStatusService(NotificationClient notificationClient) {

        this.notificationClient = notificationClient;
    }

    public List<String> getEmailAddressesAssociatedWithPermanentlyFailingNotifications() throws NotificationClientException {

        List<String> emails = new ArrayList<>();
        List<Notification> notifications = this.getPermanentlyFailingNotifications().getNotifications();

        for (Notification notification : notifications) {
            if (notification.getEmailAddress().isPresent()) {
                emails.add(notification.getEmailAddress().get());
            }
        }

        return emails;
    }

    /**
     * @return a list of notifications matching our criteria (permanent-failure and email).
     *
     * @throws NotificationClientException upon an error in GOV.UK's NotificationClient.
     */
    private NotificationList getPermanentlyFailingNotifications() throws NotificationClientException {

        return notificationClient.getNotifications(
                PERMANENT_FAILURE_MESSAGE_STATUS,
                EMAIL_MESSAGE_TYPE,
                null,
                null);
    }
}
