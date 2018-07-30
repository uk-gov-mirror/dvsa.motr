package uk.gov.dvsa.motr.notifier;

import uk.gov.dvsa.motr.config.ConfigKey;

public enum SystemVariable implements ConfigKey {

    LOG_LEVEL("LOG_LEVEL"),
    REGION("REGION"),
    DB_TABLE_SUBSCRIPTION("DB_TABLE_SUBSCRIPTION"),
    SUBSCRIPTIONS_QUEUE_URL("SUBSCRIPTIONS_QUEUE_URL"),
    MOT_TEST_REMINDER_INFO_TOKEN("MOT_TEST_REMINDER_INFO_TOKEN"),
    MOT_API_MOT_TEST_NUMBER_URI("MOT_API_MOT_TEST_NUMBER_URI"),
    MOT_API_DVLA_ID_URI("MOT_API_DVLA_ID_URI"),
    MOT_API_HGV_PSV_URI("MOT_API_HGV_PSV_URI"),
    HGV_PSV_TWO_MONTH_NOTIFICATION_TEMPLATE_ID("HGV_PSV_TWO_MONTH_NOTIFICATION_TEMPLATE_ID"),
    HGV_PSV_ONE_MONTH_NOTIFICATION_TEMPLATE_ID("HGV_PSV_ONE_MONTH_NOTIFICATION_TEMPLATE_ID"),
    SMS_HGV_PSV_TWO_MONTH_NOTIFICATION_TEMPLATE_ID("SMS_HGV_PSV_TWO_MONTH_NOTIFICATION_TEMPLATE_ID"),
    SMS_HGV_PSV_ONE_MONTH_NOTIFICATION_TEMPLATE_ID("SMS_HGV_PSV_ONE_MONTH_NOTIFICATION_TEMPLATE_ID"),
    GOV_NOTIFY_API_TOKEN("GOV_NOTIFY_API_TOKEN"),
    WORKER_COUNT("WORKER_COUNT"),
    MESSAGE_VISIBILITY_TIMEOUT("MESSAGE_VISIBILITY_TIMEOUT"),
    VEHICLE_API_CLIENT_TIMEOUT("VEHICLE_API_CLIENT_TIMEOUT"),
    MESSAGE_RECEIVE_TIMEOUT("MESSAGE_RECEIVE_TIMEOUT"),
    REMAINING_TIME_THRESHOLD("REMAINING_TIME_THRESHOLD"),
    WEB_BASE_URL("WEB_BASE_URL"),
    MOTH_DIRECT_URL_PREFIX("MOTH_DIRECT_URL_PREFIX"),
    CHECKSUM_SALT("CHECKSUM_SALT"),
    ONE_MONTH_NOTIFICATION_TEMPLATE_ID_POST_EU("ONE_MONTH_NOTIFICATION_TEMPLATE_ID_POST_EU"),
    TWO_WEEK_NOTIFICATION_TEMPLATE_ID_POST_EU("TWO_WEEK_NOTIFICATION_TEMPLATE_ID_POST_EU"),
    ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID_POST_EU("ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID_POST_EU"),
    SMS_ONE_MONTH_NOTIFICATION_TEMPLATE_ID_POST_EU("SMS_ONE_MONTH_NOTIFICATION_TEMPLATE_ID_POST_EU"),
    SMS_TWO_WEEK_NOTIFICATION_TEMPLATE_ID_POST_EU("SMS_TWO_WEEK_NOTIFICATION_TEMPLATE_ID_POST_EU"),
    SMS_ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID_POST_EU("SMS_ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID_POST_EU"),
    EU_GO_LIVE_DATE("EU_GO_LIVE_DATE");

    String value;

    SystemVariable(String name) {

        this.value = name;
    }

    public String getName() {

        return value;
    }
}
