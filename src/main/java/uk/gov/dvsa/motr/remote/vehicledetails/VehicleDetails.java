package uk.gov.dvsa.motr.remote.vehicledetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import uk.gov.dvsa.motr.serialisation.LocalDateDeserialiser;

import java.time.LocalDate;

public class VehicleDetails {

    @JsonProperty("make")
    private String make;

    @JsonProperty("model")
    private String model;

    @JsonProperty("makeInFull")
    private String makeInFull;

    @JsonProperty("primaryColour")
    private String primaryColour;

    @JsonProperty("secondaryColour")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String secondaryColour;

    @JsonProperty("registration")
    private String regNumber;

    @JsonProperty("manufactureYear")
    private Integer yearOfManufacture;

    @JsonDeserialize(using = LocalDateDeserialiser.class)
    @JsonProperty("motTestExpiryDate")
    private LocalDate motExpiryDate;

    @JsonProperty("motTestNumber")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String motTestNumber;

    @JsonProperty("dvlaId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dvlaId;

    public String getMake() {

        return make;
    }

    public VehicleDetails setMake(String make) {

        this.make = make;
        return this;
    }

    public String getModel() {

        return model;
    }

    public VehicleDetails setModel(String model) {

        this.model = model;
        return this;
    }

    public void setMakeInFull(String makeInFull) {

        this.makeInFull = makeInFull;
    }

    public String getMakeInFull() {
        return this.makeInFull;
    }

    public String getPrimaryColour() {

        return primaryColour;
    }

    public VehicleDetails setPrimaryColour(String primaryColour) {

        this.primaryColour = primaryColour;
        return this;
    }

    public String getSecondaryColour() {

        return secondaryColour;
    }

    public VehicleDetails setSecondaryColour(String secondaryColour) {

        this.secondaryColour = secondaryColour;
        return this;
    }

    public String getRegNumber() {

        return regNumber;
    }

    public VehicleDetails setRegNumber(String regNumber) {

        this.regNumber = regNumber;
        return this;
    }

    public Integer getYearOfManufacture() {

        return yearOfManufacture;
    }

    public VehicleDetails setYearOfManufacture(Integer yearOfManufacture) {

        this.yearOfManufacture = yearOfManufacture;
        return this;
    }

    public LocalDate getMotExpiryDate() {

        return motExpiryDate;
    }

    public VehicleDetails setMotExpiryDate(LocalDate motExpiryDate) {

        this.motExpiryDate = motExpiryDate;
        return this;
    }

    public String getMotTestNumber() {

        return motTestNumber;
    }

    public VehicleDetails setMotTestNumber(String motTestNumber) {

        this.motTestNumber = motTestNumber;
        return this;
    }

    public String getDvlaId() {

        return dvlaId;
    }

    public VehicleDetails setDvlaId(String dvlaId) {

        this.dvlaId = dvlaId;
        return this;
    }

    @Override
    public String toString() {
        return "VehicleDetails{" +
                "make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", primaryColour='" + primaryColour + '\'' +
                ", secondaryColour='" + secondaryColour + '\'' +
                ", regNumber='" + regNumber + '\'' +
                ", yearOfManufacture=" + yearOfManufacture +
                ", motExpiryDate=" + motExpiryDate +
                ", motTestNumber='" + motTestNumber + '\'' +
                ", dvlaId='" + dvlaId + '\'' +
                ", makeInFull='" + makeInFull + '\'' +
                '}';
    }
}
