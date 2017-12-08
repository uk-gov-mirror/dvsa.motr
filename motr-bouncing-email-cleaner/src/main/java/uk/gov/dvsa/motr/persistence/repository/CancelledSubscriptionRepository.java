package uk.gov.dvsa.motr.persistence.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.persistence.entity.SubscriptionDbItem;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CancelledSubscriptionRepository {

    private static final String PERMANENTLY_FAILING_REASON_FOR_CANCELLATION = "Permanently failing";
    private String tableName;
    private DynamoDB dynamoDb;

    public CancelledSubscriptionRepository(String tableName, String region) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    public void cancelSubscription(SubscriptionDbItem subscriptionDbItem) {
        Item item = new Item()
                .withString("id", subscriptionDbItem.getId())
                .withString("vrm", subscriptionDbItem.getVrm())
                .withString("email", subscriptionDbItem.getEmail())
                .withString("cancelled_at", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .withString("reason_for_cancellation", PERMANENTLY_FAILING_REASON_FOR_CANCELLATION);

        dynamoDb.getTable(tableName).putItem(item);
    }
}
