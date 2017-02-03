package uk.gov.dvsa.motr.web.remote.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import uk.gov.dvsa.motr.web.serialisation.LocalDateDeserialiser;

import java.time.LocalDate;

public class Vehicle {

    @JsonProperty("make")
    private String make;
    @JsonProperty("model")
    private String model;
    @JsonProperty("primaryColour")
    private String primaryColour;
    @JsonProperty("secondaryColour")
    private String secondaryColour;
    @JsonProperty("regNumber")
    private String regNumber;
    @JsonProperty("yearOfManufacture")
    private Integer yearOfManufacture;
    @JsonDeserialize(using = LocalDateDeserialiser.class)
    @JsonProperty("motExpiryDate")
    private LocalDate motExpiryDate;

    public String getMake() {

        return make;
    }

    public void setMake(String make) {

        this.make = make;
    }

    public String getModel() {

        return model;
    }

    public void setModel(String model) {

        this.model = model;
    }

    public String getPrimaryColour() {

        return primaryColour;
    }

    public void setPrimaryColour(String primaryColour) {

        this.primaryColour = primaryColour;
    }

    public String getSecondaryColour() {

        return secondaryColour;
    }

    public void setSecondaryColour(String secondaryColour) {

        this.secondaryColour = secondaryColour;
    }

    public String getRegNumber() {

        return regNumber;
    }

    public void setRegNumber(String regNumber) {

        this.regNumber = regNumber;
    }

    public Integer getYearOfManufacture() {

        return yearOfManufacture;
    }

    public void setYearOfManufacture(Integer yearOfManufacture) {

        this.yearOfManufacture = yearOfManufacture;
    }

    public LocalDate getMotExpiryDate() {

        return motExpiryDate;
    }

    public void setMotExpiryDate(LocalDate motExpiryDate) {

        this.motExpiryDate = motExpiryDate;
    }

    @Override
    public String toString() {

        return "Vehicle{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", primaryColour='" + primaryColour + '\'' +
                ", secondaryColour='" + secondaryColour + '\'' +
                ", regNumber='" + regNumber + '\'' +
                ", yearOfManufacture='" + yearOfManufacture + '\'' +
                ", motExpiryDate=" + motExpiryDate +
                '}';
    }
}
