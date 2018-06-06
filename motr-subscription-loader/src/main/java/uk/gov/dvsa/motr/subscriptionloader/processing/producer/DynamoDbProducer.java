package uk.gov.dvsa.motr.subscriptionloader.processing.producer;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.ContactDetail;
import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Produces
 */
public class DynamoDbProducer implements SubscriptionProducer {

    private static final String DUE_DATE_INDEX_NAME = "due-date-md-vehicle-type-gsi";
    private static final String DUE_DATE_AND_VEHICLE_TYPE_EXPRESSION = "mot_due_date_md = :due_date and vehicle_type = :vehicle_type";

    private String subscriptionTableName;
    private DynamoDB dynamoDb;
    private DateTimeFormatter dayMonthFormatter = DateTimeFormatter.ofPattern("MM-dd");

    public DynamoDbProducer(DynamoDB dynamoDb, String subscriptionTableName) {

        this.dynamoDb = dynamoDb;
        this.subscriptionTableName = subscriptionTableName;
    }

    public Iterator<Subscription> searchSubscriptions(List<SubscriptionCriteria> criteriaList) {
        Index dueDateIndex = dynamoDb.getTable(subscriptionTableName).getIndex(DUE_DATE_INDEX_NAME);
        // TODO do in one query

        final List<Iterator<Item>> outcomeIterators = criteriaList.stream().map(
                criteria -> searchSubscriptionsInIndex(dueDateIndex, criteria).iterator()
        ).collect(Collectors.toList());

        return new Iterator<Subscription>() {
            @Override
            public boolean hasNext() {
                return outcomeIterators.stream().anyMatch(Iterator::hasNext);
            }

            @Override
            public Subscription next() {
                Item item = outcomeIterators.stream()
                        .filter(Iterator::hasNext)
                        .findFirst()
                        .map(Iterator::next)
                        .orElseThrow(NoSuchElementException::new);

                return mapItemToSubscription(item);
            }
        };
    }

    private ItemCollection<QueryOutcome> searchSubscriptionsInIndex(Index dueDateIndex, SubscriptionCriteria criteria) {
        QuerySpec query = new QuerySpec()
                .withKeyConditionExpression(DUE_DATE_AND_VEHICLE_TYPE_EXPRESSION)
                .withValueMap(
                        new ValueMap()
                                .withString(":due_date", criteria.getTestDueDate().format(dayMonthFormatter))
                                .withString(":vehicle_type", criteria.getVehicleType().name())

                );
        return dueDateIndex.query(query);
    }

    private Subscription mapItemToSubscription(Item item) {
        LocalDate motDueDate = LocalDate.parse(item.getString("mot_due_date"), DateTimeFormatter.ISO_DATE);
        ContactDetail contactDetail = new ContactDetail(
                item.getString("email"),
                Subscription.ContactType.valueOf(item.getString("contact_type"))
        );

        Subscription subscription = new Subscription()
                .setId(item.getString("id"))
                .setVrm(item.getString("vrm"))
                .setContactDetail(contactDetail)
                .setMotTestNumber(item.getString("mot_test_number"))
                .setMotDueDate(motDueDate)
                .setVehicleType(VehicleType.getFromString(item.getString("vehicle_type")));

        if (item.isNull("mot_test_number") || !item.isPresent("mot_test_number")) {
            subscription.setDvlaId(item.getString("dvla_id"));
        }

        return subscription;
    }
}
