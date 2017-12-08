package uk.gov.dvsa.motr.smsreceiver.subscription.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import uk.gov.dvsa.motr.smsreceiver.subscription.model.MotIdentification;
import uk.gov.dvsa.motr.smsreceiver.subscription.model.Subscription;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.inject.Singleton;

@Singleton
public class DynamoDbSubscriptionRepository implements SubscriptionRepository {

    private DynamoDB dynamoDb;
    private String tableName;

    public DynamoDbSubscriptionRepository(String tableName, String region) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    @Override
    public Optional<Subscription> findByVrmAndMobileNumber(String vrm, String mobileNumber) {

        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", vrm).withString(":email", mobileNumber));

        Table table = dynamoDb.getTable(tableName);

        ItemCollection<QueryOutcome> items = table.query(query);
        Iterator<Item> resultIterator = items.iterator();

        if (!resultIterator.hasNext()) {
            return Optional.empty();
        }

        Item item = resultIterator.next();

        return Optional.of(mapItemToSubscription(item));
    }

    @Override
    public void delete(Subscription subscription) {

        PrimaryKey key = new PrimaryKey("vrm", subscription.getVrm(), "email", subscription.getContactDetail());
        Map<String, Object> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":id", subscription.getUnsubscribeId());

        dynamoDb.getTable(tableName).deleteItem(
                key,
                "id = :id",
                null,
                expressionAttributeValues
        );
    }

    private Subscription mapItemToSubscription(Item item) {

        Subscription subscription = new Subscription();
        subscription.setUnsubscribeId(item.getString("id"));
        subscription.setVrm(item.getString("vrm"));

        //we are reusing the email field for mobile number too
        subscription.setContactDetail(item.getString("email"));

        subscription.setMotDueDate(LocalDate.parse(item.getString("mot_due_date")));
        subscription.setMotIdentification(new MotIdentification(item.getString("mot_test_number"), item.getString("dvla_id")));
        return subscription;
    }
}
