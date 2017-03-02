package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.helper.UnsubscriptionUrlHelper;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SubscriptionServiceTest {

    private static final SubscriptionRepository SUBSCRIPTION_REPOSITORY = mock(SubscriptionRepository.class);
    private static final NotifyService NOTIFY_SERVICE = mock(NotifyService.class);
    private static final UnsubscriptionUrlHelper UNSUBSCRIPTION_URL_HELPER = mock(UnsubscriptionUrlHelper.class);
    private static final String TEST_REG = "TEST-REG";
    private static final String EMAIL = "TEST@TEST.com";
    private static final String UNSUBSCRIBE_LINK = "https://gov.uk";

    private SubscriptionService subscriptionService;

    @Before
    public void setUp() {

        this.subscriptionService = new SubscriptionService(SUBSCRIPTION_REPOSITORY, NOTIFY_SERVICE, UNSUBSCRIPTION_URL_HELPER);
    }

    @Test
    public void saveSubscriptionWhenSubscriptionDoesNotExistCallsDbToSaveDetails() throws Exception {

        when(SUBSCRIPTION_REPOSITORY.findByVrmAndEmail(TEST_REG, EMAIL)).thenReturn(Optional.empty());
        when(UNSUBSCRIPTION_URL_HELPER.build(any())).thenReturn(UNSUBSCRIBE_LINK);
        LocalDate date = LocalDate.now();

        this.subscriptionService.createSubscription(TEST_REG, EMAIL, date);
        verify(SUBSCRIPTION_REPOSITORY, times(1)).save(any(Subscription.class));
        verify(UNSUBSCRIPTION_URL_HELPER, times(1)).build(any());
        verify(NOTIFY_SERVICE, times(1)).sendConfirmationEmail(eq(EMAIL), eq(TEST_REG), eq(date), eq(UNSUBSCRIBE_LINK));
    }

    @Test(expected = SubscriptionAlreadyExistsException.class)
    public void throwsExceptionWhenSubscriptionAlreadyExists() throws Exception {

        when(SUBSCRIPTION_REPOSITORY.findByVrmAndEmail(TEST_REG, EMAIL)).thenReturn(Optional.of(new Subscription(UUID.randomUUID()
                .toString())));

        this.subscriptionService.createSubscription(TEST_REG, EMAIL, LocalDate.now());
        verify(SUBSCRIPTION_REPOSITORY, times(0)).save(any(Subscription.class));
    }
}
