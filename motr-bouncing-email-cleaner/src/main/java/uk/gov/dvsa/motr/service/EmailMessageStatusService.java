package uk.gov.dvsa.motr.service;

import org.joda.time.DateTime;

import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationList;

import java.util.ArrayList;
import java.util.List;

public class EmailMessageStatusService {

    public static final String PERMANENT_FAILURE_MESSAGE_STATUS = "permanent-failure";
    public static final String TEMPORARY_FAILURE_MESSAGE_STATUS = "temporary-failure";
    public static final String TECHNICAL_FAILURE_MESSAGE_STATUS = "technical-failure";
    public static final String EMAIL_MESSAGE_TYPE = "email";

    private NotificationClient notificationClient;

    public EmailMessageStatusService(NotificationClient notificationClient) {

        this.notificationClient = notificationClient;
    }

    /**
     * @return a list of emails given a specified status
     *
     * @throws NotificationClientException upon an error in GOV.UK's NotificationClient.
     */
    public List<String> getEmailAddressesAssociatedWithNotifications(String status, DateTime notificationDateFilter)
            throws NotificationClientException {

        List<String> emails = new ArrayList<>();

        for (Notification notification : this.getNotifications(status)) {

            if (notification.getEmailAddress().isPresent()
                    && notification.getCreatedAt().toLocalDate().equals(notificationDateFilter.minusDays(1).toLocalDate())) {

                emails.add(notification.getEmailAddress().get());
            }
        }

        return emails;
    }

    /**
     * @return a list of notifications
     *
     * @throws NotificationClientException upon an error in GOV.UK's NotificationClient.
     */
    private List<Notification> getNotifications(String status) throws NotificationClientException {

        List<Notification> result = new ArrayList<>();
        NotificationList page = null;

        do {
            String olderThanId = null;

            if (page != null && page.getNextPageLink().isPresent()) {

                olderThanId = page.getNotifications().get(page.getNotifications().size() - 1).getId().toString();
            }

            page = notificationClient.getNotifications(status, EMAIL_MESSAGE_TYPE, null, olderThanId);

            result.addAll(page.getNotifications());

        } while (page.getNextPageLink().isPresent() && page.getNotifications().size() == 250);

        return result;
    }
}
