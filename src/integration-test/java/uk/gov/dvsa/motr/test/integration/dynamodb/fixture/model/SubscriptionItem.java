package uk.gov.dvsa.motr.test.integration.dynamodb.fixture.model;

import com.amazonaws.services.dynamodbv2.document.Item;

import uk.gov.dvsa.motr.subscriptionloader.processing.model.Subscription;
import uk.gov.dvsa.motr.test.data.RandomDataUtil;
import uk.gov.dvsa.motr.test.integration.dynamodb.fixture.core.DynamoDbFixtureTableItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SubscriptionItem implements DynamoDbFixtureTableItem {

    private String id = UUID.randomUUID().toString();

    private LocalDate motDueDate = RandomDataUtil.dueDate();

    private String vrm = RandomDataUtil.vrm();

    private String email = RandomDataUtil.emailOrMobilePhone();

    private Subscription.ContactType contactType = RandomDataUtil.emailOrMobileContactType();

    private String motTestNumber;

    private String dvlaId;

    public SubscriptionItem generateMotTestNumber() {

        this.motTestNumber = RandomDataUtil.motTestNumber();
        return this;
    }

    public SubscriptionItem generateDvlaId() {

        this.dvlaId = RandomDataUtil.dvlaId();
        return this;
    }

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

    public Subscription.ContactType getContactType() {

        return contactType;
    }

    public SubscriptionItem setContactType(Subscription.ContactType contactType) {

        this.contactType = contactType;
        return this;
    }

    public String getMotTestNumber() {

        return this.motTestNumber;
    }

    public SubscriptionItem setMotTestNumber(String motTestNumber) {

        this.motTestNumber = motTestNumber;
        return this;
    }

    public String getDvlaId() {

        return this.dvlaId;
    }

    public SubscriptionItem setDvlaId(String dvlaId) {

        this.dvlaId = dvlaId;
        return this;
    }

    @Override
    public Item toItem() {

        return new Item().with("id", id)
                .with("mot_due_date", motDueDate.format(DateTimeFormatter.ISO_DATE))
                .with("vrm", vrm)
                .with("mot_due_date_md", motDueDate.format(DateTimeFormatter.ofPattern("MM-dd")))
                .with("email", email)
                .with("mot_test_number", motTestNumber)
                .with("contact_type", contactType.getValue())
                .with("dvla_id", dvlaId);
    }
}
