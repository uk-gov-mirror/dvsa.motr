package uk.gov.dvsa.motr.persistence.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.google.common.collect.Sets;

import uk.gov.dvsa.motr.persistence.entity.SubscriptionDbItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SubscriptionRepository {

    private String tableName;
    private DynamoDB dynamoDb;

    public SubscriptionRepository(String tableName, String region) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();

        this.dynamoDb = new DynamoDB(client);
        this.tableName = tableName;
    }

    /**
     * @param emails A list of email addresses by which to query DB.
     * @return A list of the records associated with that Email. List because email is not unique.
     */
    public List<SubscriptionDbItem> findByEmails(List<String> emails) {

        Set<String> emailsUnique = Sets.newHashSet(emails);
        List<SubscriptionDbItem> subscriptionDbItems = new ArrayList<>();

        for (String email : emailsUnique) {

            ItemCollection<QueryOutcome> items = queryOutcomeItemCollection(email);

            for (Item item : items) {
                SubscriptionDbItem subscriptionDbItem = new SubscriptionDbItem(item.getString("id"))
                        .setEmail(item.getString("email"))
                        .setMotDueDate(LocalDate.parse(item.getString("mot_due_date")))
                        .setMotTestNumber(item.getString("mot_test_number"))
                        .setVrm(item.getString("vrm"));

                subscriptionDbItems.add(subscriptionDbItem);
            }
        }

        return subscriptionDbItems;
    }

    public void deleteRecord(SubscriptionDbItem subscriptionDbItem) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey("vrm", subscriptionDbItem.getVrm(),
                "email", subscriptionDbItem.getEmail());

        dynamoDb.getTable(tableName).deleteItem(deleteItemSpec);
    }

    private ItemCollection<QueryOutcome> queryOutcomeItemCollection(String email) {

        return dynamoDb
                .getTable(tableName)
                .query(new QuerySpec().withHashKey("email", email));
    }
}
