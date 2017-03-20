package uk.gov.dvsa.motr.notifier.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;

public class DynamoDbSubscriptionRepository implements SubscriptionRepository {

    private String tableName;
    private DynamoDB dynamoDb;

    public DynamoDbSubscriptionRepository(String tableName, String region) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    public Optional<SubscriptionDbItem> findById(String subscriptionId) {

        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression("id = :id")
                .withValueMap(new ValueMap().withString(":id", subscriptionId));

        Index table = dynamoDb.getTable(tableName).getIndex("id-gsi");

        ItemCollection<QueryOutcome> items = table.query(query);
        Iterator<Item> resultIterator = items.iterator();

        if (!resultIterator.hasNext()) {
            return Optional.empty();
        }

        Item item = resultIterator.next();

        SubscriptionDbItem subscriptionDbItem = new SubscriptionDbItem(item.getString("id"));
        subscriptionDbItem.setVrm(item.getString("vrm"));
        subscriptionDbItem.setEmail(item.getString("email"));
        subscriptionDbItem.setMotDueDate(LocalDate.parse(item.getString("mot_due_date")));

        return Optional.of(subscriptionDbItem);
    }

    public void updateExpiryDate(String vrm, String email, LocalDate updatedExpiryDate) {

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("vrm", vrm, "email", email)
                .withUpdateExpression("set mot_due_date = :updatedDueDate, mot_due_date_md = :updatedDueDateMd, updated_at = :updatedAt")
                .withValueMap(new ValueMap()
                .withString(":updatedDueDate", updatedExpiryDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .withString(":updatedDueDateMd", updatedExpiryDate.format(DateTimeFormatter.ofPattern("MM-dd")))
                .withString(":updatedAt", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT)))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        dynamoDb.getTable(tableName).updateItem(updateItemSpec);
    }
}
