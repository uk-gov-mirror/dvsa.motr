package uk.gov.dvsa.motr.notifier.processing.model.notification.sms;

import uk.gov.dvsa.motr.notifier.notify.DateFormatterForSmsDisplay;
import uk.gov.dvsa.motr.notifier.processing.model.SubscriptionQueueItem;
import uk.gov.dvsa.motr.notifier.processing.model.notification.SendableNotification;
import uk.gov.dvsa.motr.vehicledetails.VehicleDetails;

import java.util.HashMap;

public abstract class SendableSmsNotification extends SendableNotification {

    public void personalise(VehicleDetails vehicleDetails) {
        personalisation = new HashMap<>();
        personalisation.put("vehicle_vrm", vehicleDetails.getRegNumber());
        personalisation.put("mot_expiry_date", DateFormatterForSmsDisplay.asFormattedForSmsDate(vehicleDetails.getMotExpiryDate()));
    }

    @Override
    public SendableSmsNotification setNotificationPathBody(String path) {
        notificationPathBody = path;
        return this;
    }

    @Override
    public SendableSmsNotification setTemplateId(String templateId) {
        super.setTemplateId(templateId);
        return this;
    }
}
