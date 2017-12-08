package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixtureTable;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.subscriptionTableName;

public class SubscriptionTable extends DynamoDbFixtureTable<SubscriptionItem> {

    public SubscriptionTable() {
        super(subscriptionTableName());
    }
}


