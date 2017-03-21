package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.test.data.RandomDataUtil;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixtureTableItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PendingSubscriptionItem implements DynamoDbFixtureTableItem {


    private String confirmationId = UUID.randomUUID().toString();

    private LocalDate motDueDate = RandomDataUtil.dueDate();

    private String vrm = RandomDataUtil.vrm();

    private String email = RandomDataUtil.email();

    public String getConfirmationId() {
        return confirmationId;
    }

    public LocalDate getMotDueDate() {
        return motDueDate;
    }

    public PendingSubscriptionItem setMotDueDate(LocalDate motDueDate) {
        this.motDueDate = motDueDate;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public PendingSubscriptionItem setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PendingSubscriptionItem setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public Item toItem() {
        return new Item().with("id", confirmationId)
                .with("mot_due_date", motDueDate.format(DateTimeFormatter.ISO_DATE))
                .with("vrm", vrm)
                .with("email", email);
    }
}
