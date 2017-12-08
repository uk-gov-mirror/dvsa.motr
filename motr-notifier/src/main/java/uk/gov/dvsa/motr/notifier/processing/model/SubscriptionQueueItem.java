package uk.gov.dvsa.motr.notifier.processing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import uk.gov.dvsa.motr.serialisation.LocalDateDeserialiser;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionQueueItem {

    public enum ContactType {

        EMAIL("EMAIL"), MOBILE("MOBILE");

        private String value;

        ContactType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @JsonProperty("id")
    private String id;

    @JsonProperty("motDueDate")
    @JsonDeserialize(using = LocalDateDeserialiser.class)
    private LocalDate motDueDate;

    @JsonProperty("vrm")
    private String vrm;

    @JsonUnwrapped
    private ContactDetail contactDetail;

    @JsonProperty("motTestNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String motTestNumber;

    @JsonProperty("dvlaId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dvlaId;

    @JsonProperty("loadedOnDate")
    @JsonDeserialize(using = LocalDateDeserialiser.class)
    private LocalDate loadedOnDate;

    private String messageReceiptHandle;

    private String messageCorrelationId;

    public String getId() {
        return id;
    }

    public SubscriptionQueueItem setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDate getMotDueDate() {
        return motDueDate;
    }

    public SubscriptionQueueItem setMotDueDate(LocalDate motDueDate) {
        this.motDueDate = motDueDate;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public SubscriptionQueueItem setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public ContactDetail getContactDetail() {
        return contactDetail;
    }

    public SubscriptionQueueItem setContactDetail(ContactDetail contactDetail) {
        this.contactDetail = contactDetail;
        return this;
    }

    public String getMotTestNumber() {
        return motTestNumber;
    }

    public SubscriptionQueueItem setMotTestNumber(String motTestNumber) {
        this.motTestNumber = motTestNumber;
        return this;
    }

    public String getDvlaId() {

        return dvlaId;
    }

    public SubscriptionQueueItem setDvlaId(String dvlaId) {

        this.dvlaId = dvlaId;
        return this;
    }

    public String getMessageReceiptHandle() {
        return messageReceiptHandle;
    }

    public SubscriptionQueueItem setMessageReceiptHandle(String messageReceiptHandle) {

        this.messageReceiptHandle = messageReceiptHandle;
        return this;
    }

    public String getMessageCorrelationId() {

        return messageCorrelationId;
    }

    public SubscriptionQueueItem setMessageCorrelationId(String messageCorrelationId) {

        this.messageCorrelationId = messageCorrelationId;
        return this;
    }

    public LocalDate getLoadedOnDate() {

        return loadedOnDate;
    }

    public SubscriptionQueueItem setLoadedOnDate(LocalDate loadedOnDate) {

        this.loadedOnDate = loadedOnDate;
        return this;
    }

    @Override
    public String toString() {
        return "SubscriptionQueueItem{" +
                "id='" + id + '\'' +
                ", motDueDate=" + motDueDate +
                ", vrm='" + vrm + '\'' +
                ", motTestNumber='" + motTestNumber + '\'' +
                ", contactType='" + contactDetail.getContactType().getValue() + '\'' +
                ", dvlaId='" + dvlaId + '\'' +
                ", email='" + contactDetail.getValue() + '\'' +
                ", loadedOnDate=" + loadedOnDate +
                ", messageReceiptHandle='" + messageReceiptHandle + '\'' +
                ", messageCorrelationId='" + messageCorrelationId + '\'' +
                '}';
    }
}
