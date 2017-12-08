package uk.gov.dvsa.motr.test.integration.fixture.model;

import uk.gov.dvsa.motr.test.integration.fixture.core.DynamoDbFixtureTable;

import static uk.gov.dvsa.motr.test.environment.variables.TestEnvironmentVariables.subscriptionTableName;

public class SubscriptionTable extends DynamoDbFixtureTable<SubscriptionItem> {

    public SubscriptionTable() {
        super(subscriptionTableName());
    }
}
