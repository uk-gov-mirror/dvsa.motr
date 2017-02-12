package uk.gov.dvsa.motr.subscriptionloader.handler;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AwsCloudwatchEvent {

    private String time;

    public AwsCloudwatchEvent setTime(String time) {
        this.time = time;
        return this;
    }

    public String getTime() {
        return time;
    }

    @JsonIgnore
    public LocalDateTime getTimeAsDateTime() {
        return LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
    }
}
