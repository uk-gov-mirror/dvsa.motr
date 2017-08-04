package uk.gov.dvsa.motr.smsreceiver.resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.smsreceiver.events.ReceivedMessageEvent;
import uk.gov.dvsa.motr.smsreceiver.events.ReceivedMessageFailedEvent;
import uk.gov.dvsa.motr.smsreceiver.model.Message;

import java.io.IOException;

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

        ObjectMapper mapper = new ObjectMapper();

        try {
            Message message = mapper.readValue(data, Message.class);
            EventLogger.logEvent(new ReceivedMessageEvent()
                    .setMessageBody(message.getMessage()).setRecipientMobile(message.getRecipientNumber()));
        } catch (IOException e) {
            EventLogger.logErrorEvent(new ReceivedMessageFailedEvent(), e);
        }
    }
}
