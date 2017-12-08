package uk.gov.dvsa.motr.handler;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.joda.time.DateTime;

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

    public String getTime() {

        return time;
    }

    public LoaderInvocationEvent setTime(String time) {

        this.time = time;
        return this;
    }

    @JsonIgnore
    public DateTime getTimeAsDateTime() {

        return DateTime.parse(time);
    }
}
