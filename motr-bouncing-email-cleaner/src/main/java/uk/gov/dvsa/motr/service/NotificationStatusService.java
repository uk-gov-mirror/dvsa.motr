package uk.gov.dvsa.motr.service;

import org.joda.time.DateTime;

import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationList;

import java.util.ArrayList;
import java.util.List;

public class NotificationStatusService {

    public static final String PERMANENT_FAILURE = "permanent-failure";
    public static final String TEMPORARY_FAILURE = "temporary-failure";
    public static final String TECHNICAL_FAILURE = "technical-failure";
    public static final String NOTIFICATION_TYPE_EMAIL = "email";
    public static final String NOTIFICATION_TYPE_SMS = "sms";
    public static final String NOTIFICATION_TYPE_ALL = null;



    private NotificationClient notificationClient;

    public NotificationStatusService(NotificationClient notificationClient) {

        this.notificationClient = notificationClient;
    }

    public List<Notification> getFilteredNotifications(String status, DateTime notificationDateFilter)
            throws NotificationClientException {

        List<Notification> notifications = new ArrayList<>();

        for (Notification notification : this.getNotifications(status, NOTIFICATION_TYPE_ALL)) {

            String notificationType = notification.getNotificationType();
            if ((notificationType.equals(NOTIFICATION_TYPE_EMAIL) || notificationType.equals(NOTIFICATION_TYPE_SMS))
                    && notification.getCreatedAt().toLocalDate().equals(notificationDateFilter.minusDays(1).toLocalDate())) {

                notifications.add(notification);
            }
        }
        return notifications;
    }

    /**
     * @return a list of notifications
     *
     * @throws NotificationClientException upon an error in GOV.UK's NotificationClient.
     */
    private List<Notification> getNotifications(String status, String notifcationType) throws NotificationClientException {

        List<Notification> result = new ArrayList<>();
        NotificationList page = null;

        do {
            String olderThanId = null;

            if (page != null && page.getNextPageLink().isPresent()) {

                olderThanId = page.getNotifications().get(page.getNotifications().size() - 1).getId().toString();
            }

            page = notificationClient.getNotifications(status, notifcationType, null, olderThanId);

            result.addAll(page.getNotifications());

        } while (page.getNextPageLink().isPresent() && page.getNotifications().size() == 250);

        return result;
    }
}
