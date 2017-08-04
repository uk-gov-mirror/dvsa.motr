package uk.gov.dvsa.motr.smsreceiver.service;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.exception.InvalidNotifyCredentialsException;
import uk.gov.dvsa.motr.smsreceiver.events.FailedToFindSubscriptionEvent;
import uk.gov.dvsa.motr.smsreceiver.events.InvalidVrmSentEvent;
import uk.gov.dvsa.motr.smsreceiver.events.UnableToProcessMessageEvent;
import uk.gov.dvsa.motr.smsreceiver.events.UserAlreadyUnsubscribedEvent;
import uk.gov.dvsa.motr.smsreceiver.model.Message;
import uk.gov.dvsa.motr.smsreceiver.subscription.model.Subscription;
import uk.gov.dvsa.motr.smsreceiver.subscription.persistence.SubscriptionRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest({EventLogger.class})
public class SmsMessageProcessorTest {

    private static final String TEST_VRM = "TEST-VRM";
    private static final String MOBILE_NUMBER = "12345678";
    private static final String TEST_TOKEN = "Bearer asdqwerty";
    private static final String BAD_TEST_TOKEN = "Bearer imabadbadtoken";

    private SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository .class);
    private CancelledSubscriptionHelper cancelledSubscriptionHelper = mock(CancelledSubscriptionHelper.class);
    private VrmValidator vrmValidator = mock(VrmValidator.class);
    private SmsMessageProcessor smsMessageProcessor;
    private SmsMessageValidator smsMessageValidator = mock(SmsMessageValidator.class);

    @Before
    public void setup() {

        PowerMockito.mockStatic(EventLogger.class);
        smsMessageProcessor = initialiseProcessor();
    }

    @Test
    public void whenMessageIsWellFormed_AndThereIsAMatchingSubscriptionToCancel_ThenSubscriptionSuccessfullyCancelled()
            throws IOException, InvalidNotifyCredentialsException {

        Subscription subscription = buildTestSubscription(TEST_VRM, MOBILE_NUMBER);

        when(smsMessageValidator.messageHasSufficientDetails(any())).thenReturn(true);
        when(vrmValidator.isValid(TEST_VRM)).thenReturn(true);
        when(subscriptionRepository.findByVrmAndMobileNumber(TEST_VRM, MOBILE_NUMBER)).thenReturn(Optional.of(subscription));
        doNothing().when(cancelledSubscriptionHelper).createANewCancelledSubscriptionEntry(subscription);
        doNothing().when(subscriptionRepository).delete(subscription);

        smsMessageProcessor = initialiseProcessor();
        smsMessageProcessor.run(buildTestRequest("STOP " + TEST_VRM));

        verify(cancelledSubscriptionHelper, times(1)).createANewCancelledSubscriptionEntry(subscription);
        verify(subscriptionRepository, times(1)).delete(subscription);
    }

    @Test
    public void whenThereAreInsufficentDetailsInTheTextMessage_ThenAnExceptionIsThrown()
            throws IOException, InvalidNotifyCredentialsException {

        smsMessageProcessor = initialiseProcessor();
        smsMessageProcessor.run(buildTestRequest("SOMETHING INSUFFICIENT"));

        verifyStatic(times(1));
        EventLogger.logEvent(isA(UnableToProcessMessageEvent.class));
    }

    @Test
    public void whenTheMessageDoesntHaveAValidVrm_ThenAnErrorEventIsLogged()
            throws IOException, InvalidNotifyCredentialsException {

        when(smsMessageValidator.messageHasSufficientDetails(any())).thenReturn(true);
        when(vrmValidator.isValid(TEST_VRM)).thenReturn(false);

        smsMessageProcessor.run(buildTestRequest("STOP " + TEST_VRM));

        verifyStatic(times(1));
        EventLogger.logEvent(isA(InvalidVrmSentEvent.class));
    }

    @Test
    public void whenNoSubscriptionIsFound_ButCancelledSubscriptionIsPresent_ThenUserAlreadyUnsubscribedEventIsLogged()
            throws IOException, InvalidNotifyCredentialsException {

        when(smsMessageValidator.messageHasSufficientDetails(any())).thenReturn(true);
        when(vrmValidator.isValid(TEST_VRM)).thenReturn(true);
        when(subscriptionRepository.findByVrmAndMobileNumber(TEST_VRM, MOBILE_NUMBER)).thenReturn(Optional.empty());
        when(cancelledSubscriptionHelper.foundMatchingCancelledSubscription(TEST_VRM, MOBILE_NUMBER)).thenReturn(true);

        smsMessageProcessor.run(buildTestRequest("STOP " + TEST_VRM));

        verifyStatic(times(1));
        EventLogger.logEvent(isA(UserAlreadyUnsubscribedEvent.class));
    }

    @Test
    public void whenNoSubscriptionIsFound_AndNoCancelledSubscriptionIsPresent_ThenFailedToFindSubscriptionEventIsLogged()
            throws IOException, InvalidNotifyCredentialsException {

        when(smsMessageValidator.messageHasSufficientDetails(any())).thenReturn(true);
        when(vrmValidator.isValid(TEST_VRM)).thenReturn(true);
        when(subscriptionRepository.findByVrmAndMobileNumber(TEST_VRM, MOBILE_NUMBER)).thenReturn(Optional.empty());
        when(cancelledSubscriptionHelper.foundMatchingCancelledSubscription(TEST_VRM, MOBILE_NUMBER)).thenReturn(false);

        smsMessageProcessor.run(buildTestRequest("STOP " + TEST_VRM));

        verifyStatic(times(1));
        EventLogger.logEvent(isA(FailedToFindSubscriptionEvent.class));
    }

    @Test
    public void testBearerTokenIsVerified()
            throws IOException, InvalidNotifyCredentialsException {

        smsMessageProcessor = initialiseProcessorWithBadToken();
        smsMessageProcessor.run(buildTestRequest("STOP" + TEST_VRM));

        verifyStatic(times(1));
        EventLogger.logEvent(isA(UnableToProcessMessageEvent.class));
    }

    private AwsProxyRequest buildTestRequest(String testSmsMessage) throws JsonProcessingException {

        Message testMessage = new Message();
        testMessage.setMessage(testSmsMessage);
        testMessage.setSubscribersMobileNumber(MOBILE_NUMBER);

        ObjectMapper mapper = new ObjectMapper();
        String testMessageAsJson = mapper.writeValueAsString(testMessage);

        AwsProxyRequest awsProxyRequest = new AwsProxyRequest();
        awsProxyRequest.setBody(testMessageAsJson);

        Map<String, String> headers = new HashMap();
        headers.put("Authorization", "Bearer " + TEST_TOKEN);
        awsProxyRequest.setHeaders(headers);

        return awsProxyRequest;
    }

    private Subscription buildTestSubscription(String vrm, String mobileNumber) {

        Subscription testSubscription = new Subscription();
        testSubscription.setVrm(vrm);
        testSubscription.setContactDetail(mobileNumber);
        return testSubscription;
    }

    private SmsMessageProcessor initialiseProcessorWithBadToken() {

        return new SmsMessageProcessor(subscriptionRepository, smsMessageValidator,
                cancelledSubscriptionHelper, BAD_TEST_TOKEN, vrmValidator, new MessageExtractor());
    }

    private SmsMessageProcessor initialiseProcessor() {

        return new SmsMessageProcessor(subscriptionRepository, smsMessageValidator,
                cancelledSubscriptionHelper, TEST_TOKEN, vrmValidator, new MessageExtractor());
    }
}
