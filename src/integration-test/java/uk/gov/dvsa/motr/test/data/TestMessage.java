package uk.gov.dvsa.motr.test.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestMessage {

    @JsonProperty("date_received")
    private String dateReceived;

    @JsonProperty("message")
    private String message;

    @JsonProperty("destination_number")
    private String destinationNumber;

    @JsonProperty("id")
    private String id;

    @JsonProperty("source_number")
    private String sourceNumber;

    @JsonProperty("date_received")
    public String getDateReceived() {

        return dateReceived;
    }

    @JsonProperty("date_received")
    public void setDateReceived(String dateReceived) {

        this.dateReceived = dateReceived;
    }

    @JsonProperty("message")
    public String getMessage() {

        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {

        this.message = message;
    }

    @JsonProperty("destination_number")
    public String getDestinationNumber() {

        return destinationNumber;
    }

    @JsonProperty("destination_number")
    public void setDestinationNumber(String destinationNumber) {

        this.destinationNumber = destinationNumber;
    }

    @JsonProperty("id")
    public String getId() {

        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {

        this.id = id;
    }

    @JsonProperty("source_number")
    public String getSourceNumber() {

        return sourceNumber;
    }

    @JsonProperty("source_number")
    public void setSourceNumber(String sourceNumber) {

        this.sourceNumber = sourceNumber;
    }
}
