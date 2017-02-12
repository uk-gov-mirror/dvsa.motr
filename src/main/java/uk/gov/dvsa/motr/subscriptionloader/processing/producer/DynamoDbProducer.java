package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Produces
 */
public class DynamoDbProducer implements SubscriptionProducer {

    private static final String INDEX_NAME = "due-date-md-gsi";
    private DynamoDB dynamoDB;

    private String subscriptionTableName;

    public DynamoDbProducer(AmazonDynamoDB dynamoDB, String subscriptionTableName) {

        this.dynamoDB = new DynamoDB(dynamoDB);
        this.subscriptionTableName = subscriptionTableName;
    }


    public Iterator<Subscription> getIterator(List<LocalDate> dates) {

        Index dueDateIndex = dynamoDB.getTable(subscriptionTableName).getIndex(INDEX_NAME);

        ItemCollection<ScanOutcome> collection = dueDateIndex.scan(new ScanFilter("mot_due_date_md").eq("02-10"));
        Iterator<Item> resultIterator = collection.iterator();

        AtomicInteger ai = new AtomicInteger(0);
        return new Iterator<Subscription>() {
            @Override
            public boolean hasNext() {
                return ai.get() < 1000 && resultIterator.hasNext();
            }

            @Override
            public Subscription next() {

                ai.incrementAndGet();
                Item item = resultIterator.next();
                String id = item.getString("id");
                String vrm = item.getString("vrm");
                String email = item.getString("email");

                return new Subscription().setId(id).setVrm(vrm).setEmail(email).setMotDueDate(LocalDate.now());
            }
        };
    }


   /* public static class Aaaa implements Runnable{
        private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd");
        int i;
        Table table;
        LocalDate ld;
        public Aaaa(int i, Table table, LocalDate ld) {
            this.i = i;
            this.table = table;
            this.ld = ld;
        }


        @Override
        public void run() {

            if(i % 100 == 0) {
                System.out.println(i);
            }
            table.putItem(new PutItemSpec().withItem(
                    new Item().withString("id", UUID.randomUUID().toString())
                            .withString("mot_due_date", ld.format(DateTimeFormatter.ISO_DATE))
                            .withString("mot_due_date_md", ld.format(dtf))
                            .withString("vrm", UUID.randomUUID().toString())
                            .withString("email", UUID.randomUUID().toString() + i + "@gmail.com")
                    )

            );
        }
    }*/
}
