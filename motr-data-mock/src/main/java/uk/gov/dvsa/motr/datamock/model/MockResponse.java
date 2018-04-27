package uk.gov.dvsa.motr.datamock.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.function.Function;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
 class MockResponse {

    @JsonProperty("make")
    public String make;
    @JsonProperty("model")
    public String model;
    @JsonProperty("primaryColour")
    public String primaryColour;
    @JsonProperty("secondaryColour")
    public String secondaryColour;
    @JsonProperty("registration")
    public String registration;
    @JsonProperty("vehicleType")
    public String vehicleType;
    @JsonProperty("manufactureYear")
    public String manufactureYear;
    @JsonProperty("motTestExpiryDate")
    public String motTestExpiryDate;
    @JsonProperty("motTestNumber")
    public String testNumber;
    @JsonProperty("vin")
    public String vin;
    @JsonProperty("dvlaId")
    public String dvlaId;
    
    public MockResponse make(String make) {
        this.make = make;
        return this;
    }

    public MockResponse model(String model) {
        this.model = model;
        return this;
    }

    public MockResponse primaryColour(String primaryColour) {
        this.primaryColour = primaryColour;
        return this;
    }

    public MockResponse vrm(String vrm) {
        this.registration = vrm;
        return this;
    }

    public MockResponse vehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType.name();
        return this;
    }

    public MockResponse manufactureYear(String manufactureYear) {
        this.manufactureYear = manufactureYear;
        return this;
    }

    public MockResponse dueDate(String dueDate) {
        this.motTestExpiryDate = dueDate;
        return this;
    }

    public MockResponse dueDate(Function<LocalDate, LocalDate> dateFn) {
        this.motTestExpiryDate = dateFn.apply(LocalDate.now()).format(ISO_LOCAL_DATE);
        return this;
    }

    public MockResponse testNumber(String testNumber) {
        this.testNumber = testNumber;
        return this;
    }

    public MockResponse secondaryColour(String secondaryColour) {
        this.secondaryColour = secondaryColour;
        return this;
    }

    public MockResponse vin(String vin) {
        this.vin = vin;
        return this;
    }

    public MockResponse dvlaId(String dvlaId) {
        this.dvlaId = dvlaId;
        return this;
    }

    public enum VehicleType {
        MOT, PSV, HGV
    }
}