package uk.gov.dvsa.motr.helper;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.kms.model.NotFoundException;

import uk.gov.dvsa.motr.config.Configurator;

import java.util.HashMap;
import java.util.Iterator;

import static java.lang.String.format;

public class DynamoDbSubscriptionHelper {
    private String tableName;
    private String tablePendingName;
    private DynamoDB dynamoDb;
    private final AmazonDynamoDB client;

    public DynamoDbSubscriptionHelper() {

        tableName = Configurator.dynamoDbTableName();
        tablePendingName = Configurator.dynamoDbTablePendingName();

        client = AmazonDynamoDBClientBuilder.standard().withRegion(Configurator.dynamoDbRegion()).build();
        dynamoDb = new DynamoDB(client);
    }

    public String findUnsubscribeIdByVrmAndEmail(String vrm, String email) {

        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression("vrm = :vrm AND email = :email")
                .withValueMap(new ValueMap().withString(":vrm", vrm).withString(":email", email));

        Table table = dynamoDb.getTable(tableName);

        ItemCollection<QueryOutcome> items = table.query(query);
        Iterator<Item> resultIterator = items.iterator();

        if (!resultIterator.hasNext()) {
            throw new NotFoundException(format("Item with vrm: %s and email %s not found", vrm, email));
        }

        Item item = resultIterator.next();

        return item.getString("id");
    }

    public String findConfirmationIdByVrmAndEmail(String vrm, String email) {

        HashMap<String, Object> valueMap = new HashMap<>();
        HashMap<String, String> nameMap = new HashMap<>();

        valueMap.put(":vrm", vrm);
        valueMap.put(":email", email);
        nameMap.put("#vrm", "vrm");
        nameMap.put("#email", "email");

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#vrm = :vrm AND #email = :email")
                .withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<ScanOutcome> result = dynamoDb.getTable(tablePendingName).scan(scanSpec);

        return result.iterator().next().get("id").toString();
    }
}
