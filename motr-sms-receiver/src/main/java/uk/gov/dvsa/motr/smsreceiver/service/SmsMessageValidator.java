package uk.gov.dvsa.motr.smsreceiver.service;


import uk.gov.dvsa.motr.smsreceiver.model.Message;

public class SmsMessageValidator {

    private static final String STOP = "stop";

    public boolean messageHasSufficientDetails(Message smsMessage) {

        return ((smsMessage.getMessage().toLowerCase().contains(STOP))
                && !smsMessage.getSubscribersMobileNumber().isEmpty());
    }
}
