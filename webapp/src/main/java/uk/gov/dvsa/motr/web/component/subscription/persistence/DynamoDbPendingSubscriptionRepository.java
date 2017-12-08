package uk.gov.dvsa.motr.web.component.subscription.persistence;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;

import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.helper.SystemVariableParam;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import static uk.gov.dvsa.motr.web.system.SystemVariable.DB_TABLE_PENDING_SUBSCRIPTION;
import static uk.gov.dvsa.motr.web.system.SystemVariable.REGION;

@Singleton
public class DynamoDbPendingSubscriptionRepository implements PendingSubscriptionRepository {

    private DynamoDB dynamoDb;
    private String tableName;

    @Inject
    public DynamoDbPendingSubscriptionRepository(
            @SystemVariableParam(DB_TABLE_PENDING_SUBSCRIPTION) String tableName,
            @SystemVariableParam(REGION) String region) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    @Override
    public Optional<PendingSubscription> findByConfirmationId(String id) {

        HashMap<String, Object> valueMap = new HashMap<>();
        HashMap<String, String> nameMap = new HashMap<>();

        valueMap.put(":id", id);
        nameMap.put("#id", "id");

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#id = :id")
                .withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<ScanOutcome> result = dynamoDb.getTable(tableName).scan(scanSpec);

        if (!result.iterator().hasNext()) {
            return Optional.empty();
        }

        Item item = result.iterator().next();

        return Optional.of(mapItemToSubscription(item));
    }

    private PendingSubscription mapItemToSubscription(Item item) {

        PendingSubscription subscription = new PendingSubscription();
        subscription.setConfirmationId(item.getString("id"));
        subscription.setVrm(item.getString("vrm"));
        subscription.setEmail(item.getString("email"));
        subscription.setMotDueDate(LocalDate.parse(item.getString("mot_due_date")));
        return subscription;
    }

    public void save(PendingSubscription subscription) {

        Item item = new Item()
                .withString("id", subscription.getConfirmationId())
                .withString("vrm", subscription.getVrm())
                .withString("email", subscription.getEmail())
                .withString("mot_due_date", subscription.getMotDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .withString("mot_due_date_md", subscription.getMotDueDate().format(DateTimeFormatter.ofPattern("MM-dd")))
                .withString("created_at", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));

        dynamoDb.getTable(tableName).putItem(item);
    }

    @Override
    public void delete(PendingSubscription subscription) {
        PrimaryKey key = new PrimaryKey("vrm", subscription.getVrm(), "email", subscription.getEmail());
        Map<String, Object> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":id", subscription.getConfirmationId());

        dynamoDb.getTable(tableName).deleteItem(
                key,
                "id = :id",
                null,
                expressionAttributeValues
        );
    }
}
