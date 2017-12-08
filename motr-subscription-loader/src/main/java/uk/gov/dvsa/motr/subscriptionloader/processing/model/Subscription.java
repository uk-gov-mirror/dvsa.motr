package uk.gov.dvsa.motr.subscriptionloader.processing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import uk.gov.dvsa.motr.subscriptionloader.serialisation.LocalDateDeserialiser;
import uk.gov.dvsa.motr.subscriptionloader.serialisation.LocalDateSerialiser;

import java.time.LocalDate;

public class Subscription {

    @JsonProperty("id")
    private String id;

    @JsonProperty("motDueDate")
    @JsonSerialize(using = LocalDateSerialiser.class)
    @JsonDeserialize(using = LocalDateDeserialiser.class)
    private LocalDate motDueDate;

    @JsonProperty("vrm")
    private String vrm;

    @JsonProperty("email")
    private String email;

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

    public String getEmail() {

        return email;
    }

    public Subscription setEmail(String email) {

        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id='" + id + '\'' +
                ", motDueDate=" + motDueDate +
                ", vrm='" + vrm + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
