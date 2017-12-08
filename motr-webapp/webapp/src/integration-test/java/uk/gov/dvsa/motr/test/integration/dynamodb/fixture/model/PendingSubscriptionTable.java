package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixtureTable;

import static uk.gov.dvsa.motr.test.integration.dynamodb.DynamoDbIntegrationHelper.pendingSubscriptionTableName;

public class PendingSubscriptionTable extends DynamoDbFixtureTable<PendingSubscriptionItem>  {

    public PendingSubscriptionTable() {
        super(pendingSubscriptionTableName());
    }
}
