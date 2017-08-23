package uk.gov.dvsa.motr.notifier.processing.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import uk.gov.dvsa.motr.serialisation.LocalDateDeserialiser;

import java.time.LocalDate;

public class SubscriptionQueueItem {

    @JsonProperty("id")
    private String id;

    @JsonProperty("motDueDate")
    @JsonDeserialize(using = LocalDateDeserialiser.class)
    private LocalDate motDueDate;

    @JsonProperty("vrm")
    private String vrm;

    @JsonProperty("email")
    private String email;

    // The NON_NULL is temporary. Should be an enum too
    @JsonProperty("contactType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contactType;

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

    public String getEmail() {
        return email;
    }

    public SubscriptionQueueItem setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getContactType() {
        return email;
    }

    public SubscriptionQueueItem setContactType(String contactType) {
        this.contactType = contactType;
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
                ", dvlaId='" + dvlaId + '\'' +
                ", email='" + email + '\'' +
                ", loadedOnDate=" + loadedOnDate +
                ", messageReceiptHandle='" + messageReceiptHandle + '\'' +
                ", messageCorrelationId='" + messageCorrelationId + '\'' +
                '}';
    }
}
