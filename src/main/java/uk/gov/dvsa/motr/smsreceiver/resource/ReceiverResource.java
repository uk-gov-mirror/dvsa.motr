package uk.gov.dvsa.motr.smsreceiver.resource;

import uk.gov.dvsa.motr.eventlog.Event;
import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.smsreceiver.events.RecceivedMessageEvent;

import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
public class ReceiverResource {

    @POST
    public void receiveMessages(String data) {

        EventLogger.logEvent(new RecceivedMessageEvent().setMessageBody(data));
    }
}
