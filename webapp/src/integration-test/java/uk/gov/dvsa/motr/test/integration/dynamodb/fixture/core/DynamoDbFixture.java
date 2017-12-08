package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.util.ArrayList;
import java.util.List;

public class DynamoDbFixture {

    private DynamoDB dynamoDb;

    public DynamoDbFixture(AmazonDynamoDB amazonDynamoDbClient) {
        dynamoDb = new DynamoDB(amazonDynamoDbClient);
    }

    private List<DynamoDbFixtureTable<? extends DynamoDbFixtureTableItem>> tables = new ArrayList<>();

    public DynamoDbFixture table(DynamoDbFixtureTable<? extends DynamoDbFixtureTableItem> table) {
        tables.add(table);
        return this;
    }

    public void run() {

        tables.forEach(t -> {
            Table dynamoTable = dynamoDb.getTable(t.name());
            t.items().forEach(i -> {

                dynamoTable.putItem(i.toItem());
            });
        });
    }
}
