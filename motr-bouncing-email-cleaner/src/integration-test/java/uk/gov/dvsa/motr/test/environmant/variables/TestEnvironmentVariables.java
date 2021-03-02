package uk.gov.dvsa.motr.test.environmant.variables;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.SystemVariable;

import java.util.Optional;

import static uk.gov.dvsa.motr.SystemVariable.DB_TABLE_CANCELLED_SUBSCRIPTION;
import static uk.gov.dvsa.motr.SystemVariable.DB_TABLE_SUBSCRIPTION;
import static uk.gov.dvsa.motr.SystemVariable.GOV_NOTIFY_API_TOKEN;
import static uk.gov.dvsa.motr.SystemVariable.LOG_LEVEL;
import static uk.gov.dvsa.motr.SystemVariable.REGION;
import static uk.gov.dvsa.motr.SystemVariable.STATUS_EMAIL_RECIPIENTS;

public class TestEnvironmentVariables extends EnvironmentVariables {

    public TestEnvironmentVariables() {

        set(LOG_LEVEL, "DEBUG");
        set(REGION, region());
        set(DB_TABLE_SUBSCRIPTION, subscriptionTableName());
        set(DB_TABLE_CANCELLED_SUBSCRIPTION, cancelledSubscriptionTableName());
        set(GOV_NOTIFY_API_TOKEN, govNotifyApiToken());
        set(STATUS_EMAIL_RECIPIENTS, "test1@email.com, test2@email.com");
    }

    public static String subscriptionTableName() {

        return lookupProperty("test.dynamoDB.integration.table.subscription");
    }

    public static String govNotifyTemplateId() {

        return lookupProperty("test.notify.template.id");
    }

    public static String cancelledSubscriptionTableName() {

        return lookupProperty("test.dynamoDB.integration.table.cancelled.subscription");
    }

    public static String govNotifyApiToken() {

        return lookupProperty("test.notify.api.integration.token");
    }

    public static String region() {

        return lookupProperty("test.dynamoDB.integration.region");
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
