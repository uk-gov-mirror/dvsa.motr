package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core;

import java.util.ArrayList;
import java.util.List;

public class DynamoDbFixtureTable<T extends DynamoDbFixtureTableItem> {

    private String name;

    public DynamoDbFixtureTable(String name) {
        this.name = name;
    }

    private List<T> items = new ArrayList<>();

    public DynamoDbFixtureTable<T> item(T item) {
        items.add(item);
        return this;
    }

    public String name() {
        return name;
    }

    List<T> items() {
        return items;
    }
}
