package uk.gov.dvsa.motr.test.integration.fixture.core;

import com.amazonaws.services.dynamodbv2.document.Item;

public interface DynamoDbFixtureTableItem {

    Item toItem();
}
