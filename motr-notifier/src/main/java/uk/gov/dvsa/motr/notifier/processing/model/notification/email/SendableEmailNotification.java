package uk.gov.dvsa.motr.notifier.processing.model.notification.email;

import com.amazonaws.util.StringUtils;

import uk.gov.dvsa.motr.notifier.processing.formatting.MakeModelFormatter;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.SendableNotification;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.util.HashMap;

import javax.ws.rs.core.UriBuilder;

public abstract class SendableEmailNotification extends SendableNotification {

    public static final String UNSUBSCRIBE_LINK_KEY = "unsubscribe_link";
    public static final String VEHICLE_DETAILS_KEY = "vehicle_details";

    public static final String PRESERVATION_STATEMENT_SUFFIX = " for next year.";
    public static final String MOT_PRESERVATION_STATEMENT_PREFIX =
            "You can get your MOT test done from tomorrow to keep the same MOT test date ";

    private String webBaseUrl;
    private String notificationPathSubject;

    protected SendableEmailNotification(String webBaseUrl) {
        this.webBaseUrl = webBaseUrl;
    }

    public void personalise(SubscriptionQueueItem subscription, VehicleDetails vehicleDetails) {
        String vehicleDetailsString = MakeModelFormatter.getMakeModelDisplayStringFromVehicleDetails(vehicleDetails,
                ", ") + vehicleDetails.getRegNumber();

        String unsubscribeLink = UriBuilder.fromPath(this.webBaseUrl).path("unsubscribe").path(subscription.getId()).build().toString();

        personalisation = new HashMap<>();

        personalisation.put(VEHICLE_DETAILS_KEY, vehicleDetailsString);
        personalisation.put(UNSUBSCRIBE_LINK_KEY, unsubscribeLink);
    }

    @Override
    public SendableEmailNotification setNotificationPathBody(String path) {
        notificationPathBody = path;
        return this;
    }

    @Override
    public SendableEmailNotification setTemplateId(String templateId) {
        super.setTemplateId(templateId);
        return this;
    }

    public String getNotificationPathSubject() {
        return this.notificationPathSubject;
    }

    public SendableEmailNotification setNotificationPathSubject(String path) {
        this.notificationPathSubject = path;
        return this;
    }

    boolean vehicleHadItsFirstMotTest(VehicleDetails vehicleDetails) {
        return !StringUtils.isNullOrEmpty(vehicleDetails.getMotTestNumber());
    }
}
