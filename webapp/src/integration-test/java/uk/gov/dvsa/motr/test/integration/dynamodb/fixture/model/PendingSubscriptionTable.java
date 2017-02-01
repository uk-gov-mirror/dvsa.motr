package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixtureTable;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.tableName;

public class PendingSubscriptionTable extends DynamoDbFixtureTable<PendingSubscriptionItem>  {

    public PendingSubscriptionTable() {
        super(tableName("pending_subscription"));
    }
}
