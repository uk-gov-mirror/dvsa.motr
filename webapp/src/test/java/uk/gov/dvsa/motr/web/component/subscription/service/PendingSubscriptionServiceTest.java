package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.helper.EmailConfirmationUrlHelper;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import static java.util.Optional.empty;

public class PendingSubscriptionServiceTest {

    private static final PendingSubscriptionRepository PENDING_SUBSCRIPTION_REPOSITORY = mock(PendingSubscriptionRepository.class);
    private static final SubscriptionRepository SUBSCRIPTION_REPOSITORY = mock(SubscriptionRepository.class);
    private static final NotifyService NOTIFY_SERVICE = mock(NotifyService.class);
    private static final EmailConfirmationUrlHelper EMAIL_CONFIRMATION_URL_HELPER = mock(EmailConfirmationUrlHelper.class);

    private static final String TEST_VRM = "TEST-REG";
    private static final String EMAIL = "TEST@TEST.com";
    private static final String CONFIRMATION_LINK = "CONFIRMATION_LINK";

    private PendingSubscriptionService subscriptionService;

    @Before
    public void setUp() {

        this.subscriptionService = new PendingSubscriptionService(
                PENDING_SUBSCRIPTION_REPOSITORY,
                SUBSCRIPTION_REPOSITORY,
                NOTIFY_SERVICE,
                EMAIL_CONFIRMATION_URL_HELPER
        );

        when(EMAIL_CONFIRMATION_URL_HELPER.build(any(PendingSubscription.class))).thenReturn(CONFIRMATION_LINK);
    }

    @Test
    public void saveSubscriptionCallsDbToSaveDetailsAndSendsNotification() throws Exception {

        withExpectedSubscription(empty());
        when(PENDING_SUBSCRIPTION_REPOSITORY.findById("Asd")).thenReturn(empty());
        doNothing().when(NOTIFY_SERVICE).sendEmailAddressConfirmationEmail(EMAIL, CONFIRMATION_LINK);
        LocalDate date = LocalDate.now();

        this.subscriptionService.createPendingSubscription(TEST_VRM, EMAIL, date);
        verify(PENDING_SUBSCRIPTION_REPOSITORY, times(1)).save(any(PendingSubscription.class));
        verify(NOTIFY_SERVICE, times(1)).sendEmailAddressConfirmationEmail(EMAIL, CONFIRMATION_LINK);
    }

    @Test(expected = RuntimeException.class)
    public void whenDbSaveFailsConfirmationEmailIsNotSent() throws Exception {

        withExpectedSubscription(empty());
        doThrow(new RuntimeException()).when(PENDING_SUBSCRIPTION_REPOSITORY).save(any(PendingSubscription.class));
        LocalDate date = LocalDate.now();

        this.subscriptionService.createPendingSubscription(TEST_VRM, EMAIL, date);
        verify(PENDING_SUBSCRIPTION_REPOSITORY, times(1)).save(any(PendingSubscription.class));
        verifyZeroInteractions(NOTIFY_SERVICE);
    }

    @Test(expected = SubscriptionAlreadyExistsException.class)
    public void expectSubscriptionAlreadyExistsExceptionWhenActiveSubscriptionAlreadyExists() throws Exception {

        Optional<Subscription> existingSubscription = Optional.of(new Subscription("id"));
        withExpectedSubscription(existingSubscription);
        LocalDate date = LocalDate.now();

        subscriptionService.createPendingSubscription(TEST_VRM, EMAIL, date);
    }

    private void withExpectedSubscription(Optional<Subscription> subscription) {
        when(SUBSCRIPTION_REPOSITORY.findByVrmAndEmail(TEST_VRM, EMAIL)).thenReturn(subscription);
    }

}
