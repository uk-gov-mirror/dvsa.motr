package uk.gov.dvsa.motr.eventlog;


public class TimedEvent extends Event {

    @Override
    public String getCode() {
        return "TIMED";
    }

    public TimedEvent setTime(long time) {

        params.put("time-ms", String.valueOf(time));
        return this;
    }

    public TimedEvent setName(String name) {
        params.put("name", name);
        return this;
    }
}
