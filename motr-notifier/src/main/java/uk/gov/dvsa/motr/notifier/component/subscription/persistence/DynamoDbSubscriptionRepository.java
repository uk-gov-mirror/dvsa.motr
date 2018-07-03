package uk.gov.dvsa.motr.notifier.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;

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

        if (item.isPresent("dvla_id")) {
            subscriptionDbItem.setDvlaId(item.getString("dvla_id"));
        } else {
            subscriptionDbItem.setMotTestNumber(item.getString("mot_test_number"));
        }

        subscriptionDbItem.setVehicleType(VehicleType.getFromString(item.getString("vehicle_type")));

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

    public void updateMotTestNumber(String vrm, String email, String updatedMotTestNumber) {

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("vrm", vrm, "email", email)
                .withUpdateExpression("SET mot_test_number = :updatedMotTestNumber, updated_at = :updatedAt REMOVE dvla_id")
                .withValueMap(new ValueMap()
                .withString(":updatedMotTestNumber", updatedMotTestNumber)
                .withString(":updatedAt", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT)))
                .withReturnValues(ReturnValue.ALL_NEW);

        dynamoDb.getTable(tableName).updateItem(updateItemSpec);
    }

    public void updateVrm(String vrm, String email, String updatedVrm) throws Exception {

        try {
            GetItemSpec getItemSpec = new GetItemSpec().withPrimaryKey("vrm", vrm, "email", email);
            Item originalItem = dynamoDb.getTable(tableName).getItem(getItemSpec);

            // Because vrm is part of primary key, cannot update it. Instead, delete old record and create new one.
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey("vrm", vrm, "email", email);
            dynamoDb.getTable(tableName).deleteItem(deleteItemSpec);

            Item item = new Item()
                    .withPrimaryKey("vrm", updatedVrm, "email", email)
                    .withString("id", originalItem.getString("id"))
                    .withString("mot_due_date", originalItem.getString("mot_due_date"))
                    .withString("mot_due_date_md", originalItem.getString("mot_due_date_md"))
                    .withString("created_at", originalItem.getString("created_at"))
                    .withString("updated_at", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                    .withString("contact_type", originalItem.getString("contact_type"))
                    .withString("vehicle_type", originalItem.getString("vehicle_type"));

            if (originalItem.isPresent("dvla_id")) {
                item.withString("dvla_id", originalItem.getString("dvla_id"));
            } else {
                item.withString("mot_test_number", originalItem.getString("mot_test_number"));
            }

            dynamoDb.getTable(tableName).putItem(item);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void deleteSubscription(String vrm, String email) {

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey("vrm", vrm, "email", email);
        dynamoDb.getTable(tableName).deleteItem(deleteItemSpec);
    }
}
