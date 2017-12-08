package uk.gov.dvsa.motr.test.integration.lambda;

/**
 * An event that arrives to Loader to invoke subscription loading.
 * Example format:
 * {
 *     "time" : "2017-01-02T12:00:00Z", // required
 *     "purge" : false                  // optional
 * }
 */
public class LoaderInvocationEvent {

    private String time;
    private boolean isPurge = false;

    public LoaderInvocationEvent(String time) {

        this.time = time;
    }

    public String getTime() {

        return time;
    }

    public boolean isPurge() {

        return isPurge;
    }
}
