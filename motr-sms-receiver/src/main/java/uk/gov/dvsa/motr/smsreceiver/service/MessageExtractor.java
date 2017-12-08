package uk.gov.dvsa.motr.smsreceiver.service;


public class MessageExtractor {

    private static final String STOP = "stop";

    /**
     * pull the remainder of the string after "stop" and trim it - this should be the reg number
     *
     * @param messageBody the actual text of the sms sent
     * @return the vrm extracted from the message
     */
    public String getVrmFromMesageBody(String messageBody) {

        int startIndexOfStop = messageBody.toLowerCase().indexOf(STOP);
        return (messageBody.length() > STOP.length() && startIndexOfStop >= 0) ? messageBody.substring(startIndexOfStop + 4).trim() : "";
    }

    public String getMobileNumberWithoutInternationalCode(String mobileNumber) {

        if (mobileNumber.startsWith("44")) {
            return "0" + mobileNumber.substring(2);
        } else {
            return mobileNumber;
        }
    }
}
