package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;


public class DynamoDbSubscriptionRepository implements SubscriptionRepository {
    
    private DynamoDB dynamoDb;

    private String tableName;

    public DynamoDbSubscriptionRepository(AmazonDynamoDB dynamoDb, String tableName) {
        this.dynamoDb = new DynamoDB(dynamoDb);
        this.tableName = tableName;
    }

    public Optional<Subscription> findById(String subscriptionId) {

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

        Subscription subscription = new Subscription(item.getString("id"));
        subscription.setVrm(item.getString("vrm"));
        subscription.setEmail(item.getString("email"));
        subscription.setMotDueDate(LocalDate.parse(item.getString("mot_due_date")));

        return Optional.of(subscription);
    }

    public Optional<Subscription> findByVrmAndEmail(String vrm, String email) {
        return Optional.empty();
    }

    public void save(Subscription subscription) {

    }
}
