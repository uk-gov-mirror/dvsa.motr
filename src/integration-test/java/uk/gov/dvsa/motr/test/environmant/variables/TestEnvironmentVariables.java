package uk.gov.dvsa.motr.test.environmant.variables;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.notifier.SystemVariable;

import java.util.Optional;

import static uk.gov.dvsa.motr.notifier.SystemVariable.DB_TABLE_SUBSCRIPTION;
import static uk.gov.dvsa.motr.notifier.SystemVariable.GOV_NOTIFY_API_TOKEN;
import static uk.gov.dvsa.motr.notifier.SystemVariable.LOG_LEVEL;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MESSAGE_RECEIVE_TIMEOUT;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MESSAGE_VISIBILITY_TIMEOUT;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MOT_API_MOT_TEST_NUMBER_URI;
import static uk.gov.dvsa.motr.notifier.SystemVariable.MOT_TEST_REMINDER_INFO_TOKEN;
import static uk.gov.dvsa.motr.notifier.SystemVariable.ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.notifier.SystemVariable.ONE_MONTH_NOTIFICATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.notifier.SystemVariable.REGION;
import static uk.gov.dvsa.motr.notifier.SystemVariable.REMAINING_TIME_THRESHOLD;
import static uk.gov.dvsa.motr.notifier.SystemVariable.SUBSCRIPTIONS_QUEUE_URL;
import static uk.gov.dvsa.motr.notifier.SystemVariable.TWO_WEEK_NOTIFICATION_TEMPLATE_ID;
import static uk.gov.dvsa.motr.notifier.SystemVariable.VEHICLE_API_CLIENT_TIMEOUT;
import static uk.gov.dvsa.motr.notifier.SystemVariable.WEB_BASE_URL;
import static uk.gov.dvsa.motr.notifier.SystemVariable.WORKER_COUNT;

public class TestEnvironmentVariables extends EnvironmentVariables {

    public TestEnvironmentVariables() {

        set(LOG_LEVEL, "INFO");
        set(REGION, region());
        set(DB_TABLE_SUBSCRIPTION, subscriptionTableName());
        set(SUBSCRIPTIONS_QUEUE_URL, sqsEndpoint());
        set(MOT_TEST_REMINDER_INFO_TOKEN, motTestReminderInfoToken());
        set(MOT_API_MOT_TEST_NUMBER_URI, motTestNumberApiEndpoint());
        set(ONE_MONTH_NOTIFICATION_TEMPLATE_ID, notifyOneMonthTemplateId());
        set(TWO_WEEK_NOTIFICATION_TEMPLATE_ID, notifyTwoWeekTemplateId());
        set(ONE_DAY_AFTER_NOTIFICATION_TEMPLATE_ID, notifyOneDayAfterTemplateId());
        set(GOV_NOTIFY_API_TOKEN, govNotifyApiToken());
        set(WORKER_COUNT, "1");
        set(MESSAGE_RECEIVE_TIMEOUT, "10");
        set(MESSAGE_VISIBILITY_TIMEOUT, "60");
        set(REMAINING_TIME_THRESHOLD, "20");
        set(VEHICLE_API_CLIENT_TIMEOUT, "10");
        set(WEB_BASE_URL, "");
    }

    public static String motTestNumberApiEndpoint() {
        return lookupProperty("test.mottestnumber.api.integration.endpoint");
    }

    public static String subscriptionTableName() {

        return lookupProperty("test.dynamoDB.integration.table.subscription");
    }

    public static String region() {

        return lookupProperty("test.dynamoDB.integration.region");
    }

    public static String loaderFunctionName() {

        return lookupProperty("test.lambda.integration.loaderfunction");
    }

    public static String sqsEndpoint() {

        return lookupProperty("test.sqs.integration.endpoint");
    }

    public static String govNotifyApiToken() {

        return lookupProperty("test.notify.api.integration.token");
    }

    public static String motTestReminderInfoToken() {

        return lookupProperty("test.vehicle.api.integration.token");
    }

    public static String notifyTwoWeekTemplateId() {

        return lookupProperty("test.notify.template.two.week");
    }

    public static String notifyOneMonthTemplateId() {

        return lookupProperty("test.notify.template.one.month");
    }

    public static String notifyOneDayAfterTemplateId() {

        return lookupProperty("test.notify.template.one.day.after");
    }

    private static String lookupProperty(String property) {

        return Optional.ofNullable(System.getProperty(property)).orElseThrow(
                () -> new RuntimeException("Property: " + property + " is not defined!")
        );
    }

    private void set(SystemVariable var, String value) {

        set(var.getName(), value);
    }

}
