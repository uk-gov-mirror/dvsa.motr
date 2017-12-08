package uk.gov.dvsa.motr.smsreceiver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import uk.gov.dvsa.motr.smsreceiver.serialisation.LocalDateDeserialiser;

import java.io.Serializable;
import java.time.LocalDate;

public class Message implements Serializable {

    @JsonDeserialize(using = LocalDateDeserialiser.class)
    @JsonProperty("date_received")
    private LocalDate dateReceived;

    @JsonProperty("message")
    private String message;

    @JsonProperty("destination_number")
    private String destinationNumber;

    @JsonProperty("id")
    private String id;

    @JsonProperty("source_number")
    private String subscribersMobileNumber;

    public LocalDate getDateReceived() {

        return dateReceived;
    }

    public void setDateReceived(LocalDate dateReceived) {

        this.dateReceived = dateReceived;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public String getDestinationNumber() {

        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {

        this.destinationNumber = destinationNumber;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getSubscribersMobileNumber() {

        return subscribersMobileNumber;
    }

    public void setSubscribersMobileNumber(String receiptNumber) {

        this.subscribersMobileNumber = receiptNumber;
    }

    @Override
    public String toString() {

        return "Message{" +
                "dateReceived='" + dateReceived + '\'' +
                ", message='" + message + '\'' +
                ", destinationNumber='" + destinationNumber + '\'' +
                ", id='" + id + '\'' +
                ", receiptNumber='" + subscribersMobileNumber + '\'' +
                '}';
    }
}
