package uk.gov.dvsa.motr.notifier.processing.model.notification;

import uk.gov.dvsa.motr.notifier.events.NotifyEvent;

import java.util.Map;

public abstract class SendableNotification {

    protected static final String HGV_PSV_DIRECTORY = "hgv-psv/";

    protected String notificationPathBody;
    private String templateId;
    protected Map<String,String> personalisation;

    public Map<String, String> getPersonalisation() {
        return personalisation;
    }

    public String getNotificationPathBody() {
        return this.notificationPathBody;
    }

    public SendableNotification setNotificationPathBody(String path) {
        this.notificationPathBody = path;
        return this;
    }

    public abstract NotifyEvent getEvent();

    public String getTemplateId() {
        return templateId;
    }

    public SendableNotification setTemplateId(String templateId) {
        this.templateId = templateId;
        return this;
    }
}
