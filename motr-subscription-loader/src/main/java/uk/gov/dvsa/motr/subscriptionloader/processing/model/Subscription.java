package uk.gov.dvsa.motr.subscriptionloader.processing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import uk.gov.dvsa.motr.subscriptionloader.serialisation.LocalDateDeserialiser;
import uk.gov.dvsa.motr.subscriptionloader.serialisation.LocalDateSerialiser;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription {

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
    @JsonSerialize(using = LocalDateSerialiser.class)
    @JsonDeserialize(using = LocalDateDeserialiser.class)
    private LocalDate motDueDate;

    @JsonProperty("vrm")
    private String vrm;

    @JsonUnwrapped
    private ContactDetail contactDetail;

    @JsonProperty("motTestNumber")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String motTestNumber;

    @JsonProperty("dvlaId")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String dvlaId;

    @JsonProperty("loadedOnDate")
    @JsonSerialize(using = LocalDateSerialiser.class)
    @JsonDeserialize(using = LocalDateDeserialiser.class)
    private LocalDate loadedOnDate;

    public String getId() {

        return id;
    }

    public Subscription setId(String id) {

        this.id = id;
        return this;
    }

    public LocalDate getMotDueDate() {

        return motDueDate;
    }

    public Subscription setMotDueDate(LocalDate motDueDate) {

        this.motDueDate = motDueDate;
        return this;
    }

    public String getVrm() {

        return vrm;
    }

    public Subscription setVrm(String vrm) {

        this.vrm = vrm;
        return this;
    }

    public ContactDetail getContactDetail() {

        return contactDetail;
    }

    public Subscription setContactDetail(ContactDetail contactDetail) {

        this.contactDetail = contactDetail;
        return this;
    }

    public String getMotTestNumber() {

        return motTestNumber;
    }

    public Subscription setMotTestNumber(String motTestNumber) {

        this.motTestNumber = motTestNumber;
        return this;
    }

    public String getDvlaId() {

        return dvlaId;
    }

    public Subscription setDvlaId(String dvlaId) {

        this.dvlaId = dvlaId;
        return this;
    }

    public LocalDate getLoadedOnDate() {

        return loadedOnDate;
    }

    public Subscription setLoadedOnDate(LocalDate loadedOnDate) {

        this.loadedOnDate = loadedOnDate;
        return this;
    }

    @Override
    public String toString() {

        return "Subscription{" +
                "id='" + id + '\'' +
                ", motDueDate=" + motDueDate +
                ", vrm='" + vrm + '\'' +
                ", email='" + contactDetail.getValue() + '\'' +
                ", contactType='" + contactDetail.getContactType().getValue() + '\'' +
                ", motTestNumber='" + motTestNumber + '\'' +
                ", dvlaId='" + dvlaId + '\'' +
                ", loadedOnDate=" + loadedOnDate +
                '}';
    }
}
