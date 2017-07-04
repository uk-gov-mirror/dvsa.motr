package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.test.data.RandomDataUtil;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixtureTableItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SubscriptionItem implements DynamoDbFixtureTableItem {

    private String id = UUID.randomUUID().toString();

    private LocalDate motDueDate = RandomDataUtil.dueDate();

    private String vrm = "OLD-EXPIRY-" + RandomDataUtil.vrm();

    private String email = RandomDataUtil.email();

    private String motTestNumber = RandomDataUtil.motTestNumber();

    public String getId() {
        return id;
    }

    public LocalDate getMotDueDate() {
        return motDueDate;
    }

    public SubscriptionItem setMotDueDate(LocalDate motDueDate) {
        this.motDueDate = motDueDate;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public SubscriptionItem setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public SubscriptionItem setEmail(String email) {
        this.email = email;
        return this;
    }

    public String motTestNumber() {
        return motTestNumber;
    }

    public SubscriptionItem setMotTestNumber(String motTestNumber) {
        this.motTestNumber = motTestNumber;
        return this;
    }

    @Override
    public Item toItem() {

        return new Item().with("id", id)
                .with("mot_due_date", motDueDate.format(DateTimeFormatter.ISO_DATE))
                .with("vrm", vrm)
                .with("mot_test_number", motTestNumber)
                .with("mot_due_date_md", motDueDate.format(DateTimeFormatter.ofPattern("MM-dd")))
                .with("email", email);
    }
}
