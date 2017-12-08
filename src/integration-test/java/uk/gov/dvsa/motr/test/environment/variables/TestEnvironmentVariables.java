package uk.gov.dvsa.motr.test.environment.variables;


import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.smsreceiver.system.SystemVariable;

import java.util.Optional;

import static uk.gov.dvsa.motr.smsreceiver.system.SystemVariable.DB_TABLE_CANCELLED_SUBSCRIPTION;
import static uk.gov.dvsa.motr.smsreceiver.system.SystemVariable.DB_TABLE_SUBSCRIPTION;
import static uk.gov.dvsa.motr.smsreceiver.system.SystemVariable.GOV_NOTIFY_API_TOKEN;
import static uk.gov.dvsa.motr.smsreceiver.system.SystemVariable.LOG_LEVEL;
import static uk.gov.dvsa.motr.smsreceiver.system.SystemVariable.NOTIFY_BEARER_TOKEN;
import static uk.gov.dvsa.motr.smsreceiver.system.SystemVariable.REGION;
import static uk.gov.dvsa.motr.smsreceiver.system.SystemVariable.SMS_UNSUBSCRIPTION_CONFIRMATION_TEMPLATE_ID;

public class TestEnvironmentVariables extends EnvironmentVariables {

    public TestEnvironmentVariables() {

        set(LOG_LEVEL, "INFO");
        set(REGION, region());
        set(DB_TABLE_SUBSCRIPTION, subscriptionTableName());
        set(DB_TABLE_CANCELLED_SUBSCRIPTION, cancelledSubscriptionTableName());
        set(NOTIFY_BEARER_TOKEN, lambdaEncryptedToken());
        set(GOV_NOTIFY_API_TOKEN, govNotifyApiToken());
        set(SMS_UNSUBSCRIPTION_CONFIRMATION_TEMPLATE_ID, notifySmsUnsubscriptionConfirmationTemplateId());
    }

    private void set(SystemVariable var, String value) {

        set(var.getName(), value);
    }

    public static String region() {

        return lookupProperty("test.dynamoDB.integration.region");
    }

    public static String subscriptionTableName() {

        return lookupProperty("test.dynamoDB.integration.table.subscription");
    }

    public static String cancelledSubscriptionTableName() {

        return lookupProperty("test.dynamoDB.integration.table.cancellation");
    }

    public static String token() {

        return lookupProperty("test.notify.token");
    }

    public static String lambdaEncryptedToken() {

        return lookupProperty("test.lambda.integration.token");
    }

    public static String govNotifyApiToken() {

        return lookupProperty("test.notify.api.integration.token");
    }

    public static String notifySmsUnsubscriptionConfirmationTemplateId() {

        return lookupProperty("test.notify.template.unsubscription.confirmation");
    }

    public static String handlerFunctionName() {

        return lookupProperty("test.lambda.integration.handlerFunctionName");
    }

    private static String lookupProperty(String property) {

        return Optional.ofNullable(System.getProperty(property))
                .orElseThrow(() -> new RuntimeException("Property: " + property + " is not defined!")
        );
    }
}
