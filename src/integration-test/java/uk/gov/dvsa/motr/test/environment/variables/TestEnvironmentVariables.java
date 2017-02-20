package uk.gov.dvsa.motr.test.environment.variables;

import org.junit.contrib.java.lang.system.EnvironmentVariables;

import uk.gov.dvsa.motr.subscriptionloader.SystemVariable;
import uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper;
import uk.gov.dvsa.motr.test.integration.sqs.SqsHelper;

import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.INFLIGHT_BATCHES;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.LOG_LEVEL;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.POST_PURGE_DELAY;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.QUEUE_URL;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.REGION;
import static uk.gov.dvsa.motr.subscriptionloader.SystemVariable.TABLE_NAME;

public class TestEnvironmentVariables extends EnvironmentVariables {

    public TestEnvironmentVariables() {

        logLevel("INFO");
        region(DynamoDbIntegrationHelper.region());
        subscriptionTable(DynamoDbIntegrationHelper.subscriptionTableName());
        inflightBatches(SqsHelper.inflightBatches());
        subscriptionQueue(SqsHelper.sqsEndpoint());
        postPurgeDelay(SqsHelper.postPurgeDelay());
    }

    private TestEnvironmentVariables logLevel(String value) {

        return set(LOG_LEVEL, value);
    }

    private TestEnvironmentVariables region(String value) {

        return set(REGION, value);
    }

    private TestEnvironmentVariables inflightBatches(String value) {

        return set(INFLIGHT_BATCHES, value);
    }

    private TestEnvironmentVariables subscriptionTable(String value) {

        return set(TABLE_NAME, value);
    }

    private TestEnvironmentVariables subscriptionQueue(String value) {

        return set(QUEUE_URL, value);
    }

    private TestEnvironmentVariables postPurgeDelay(String value) {

        return set(POST_PURGE_DELAY, value);
    }

    private TestEnvironmentVariables set(SystemVariable var, String value) {
        set(var.getName(), value);
        return this;
    }
}
