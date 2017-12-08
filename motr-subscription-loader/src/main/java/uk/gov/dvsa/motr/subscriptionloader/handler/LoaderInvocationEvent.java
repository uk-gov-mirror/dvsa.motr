package uk.gov.dvsa.motr.subscriptionloader.handler;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An event that arrives to Loader to invoke subscription loading.
 * Example format:
 * {
 *     "time" : "2017-01-02T12:00:00Z", // required
 *     "purge" : false                  // optional
 * }
 */
public class LoaderInvocationEvent {

    /**
     * Reference date and time against which Lambda is invoked. In the situation where items from previous day have to be processed,
     * it is enough to change this timestamp to point to the specific date.
     */
    private String time;

    /**
     * When flag is set to false purging process is suppressed.
     * That may come handy when it is necessary to run Lambda manually without affecting existing queue.
     * It also makes testing easier.
     */
    private boolean isPurge = true;

    public String getTime() {

        return time;
    }

    public boolean isPurge() {

        return isPurge;
    }

    public LoaderInvocationEvent setTime(String time) {

        this.time = time;
        return this;
    }

    public LoaderInvocationEvent setPurge(boolean purge) {
        this.isPurge = purge;
        return this;
    }

    @JsonIgnore
    public LocalDateTime getTimeAsDateTime() {

        return LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
    }
}
