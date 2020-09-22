package uk.gov.dvsa.motr.test.environment.variables;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.subscriptionloader.SystemVariable;

import java.util.Optional;

import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.HGV_PSV_SUBSCRIPTION_LOADER;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.INFLIGHT_BATCHES;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.LOG_LEVEL;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.POST_PURGE_DELAY;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.QUEUE_URL;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.REGION;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.TABLE_NAME;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.region;
import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.subscriptionTableName;
import static uk.gov.dvsa.motr.test.integration.sqs.SqsHelper.sqsEndpoint;

public class TestEnvironmentVariables extends EnvironmentVariables {

    public TestEnvironmentVariables() {

        set(LOG_LEVEL, "INFO");
        set(REGION, region());
        set(TABLE_NAME, subscriptionTableName());
        set(QUEUE_URL, sqsEndpoint());
        set(POST_PURGE_DELAY, "0");
        set(INFLIGHT_BATCHES, "1");
        set(HGV_PSV_SUBSCRIPTION_LOADER, hgvPsvSubscriptionLoader());
    }


    public static String hgvPsvSubscriptionLoader() {

        return lookupProperty("test.hgv.psv.subscription.loader");
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
