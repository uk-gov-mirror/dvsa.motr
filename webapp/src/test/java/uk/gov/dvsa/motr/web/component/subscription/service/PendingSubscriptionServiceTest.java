package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.SubscriptionAlreadyExistsException;
import uk.gov.dvsa.motr.web.component.subscription.helper.EmailConfirmationUrlHelper;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;

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

    private final PendingSubscriptionRepository pendingSubscriptionRepository = mock(PendingSubscriptionRepository.class);
    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final NotifyService notifyService = mock(NotifyService.class);
    private final EmailConfirmationUrlHelper emailConfirmationUrlHelper = mock(EmailConfirmationUrlHelper.class);

    private static final String TEST_VRM = "TEST-REG";
    private static final String EMAIL = "TEST@TEST.com";
    private static final String CONFIRMATION_LINK = "CONFIRMATION_LINK";

    private PendingSubscriptionService subscriptionService;

    @Before
    public void setUp() {

        this.subscriptionService = new PendingSubscriptionService(
                pendingSubscriptionRepository,
                subscriptionRepository,
                notifyService,
                emailConfirmationUrlHelper
        );

        when(emailConfirmationUrlHelper.build(any(PendingSubscription.class))).thenReturn(CONFIRMATION_LINK);
    }

    @Test
    public void saveSubscriptionCallsDbToSaveDetailsAndSendsNotification() throws Exception {

        withExpectedSubscription(empty());
        when(pendingSubscriptionRepository.findByConfirmationId("Asd")).thenReturn(empty());
        doNothing().when(notifyService).sendEmailAddressConfirmationEmail(EMAIL, CONFIRMATION_LINK);
        LocalDate date = LocalDate.now();

        this.subscriptionService.createPendingSubscription(TEST_VRM, EMAIL, date);
        verify(pendingSubscriptionRepository, times(1)).save(any(PendingSubscription.class));
        verify(notifyService, times(1)).sendEmailAddressConfirmationEmail(EMAIL, CONFIRMATION_LINK);
    }

    @Test(expected = RuntimeException.class)
    public void whenDbSaveFailsConfirmationEmailIsNotSent() throws Exception {

        withExpectedSubscription(empty());
        doThrow(new RuntimeException()).when(pendingSubscriptionRepository).save(any(PendingSubscription.class));
        LocalDate date = LocalDate.now();

        this.subscriptionService.createPendingSubscription(TEST_VRM, EMAIL, date);
        verify(pendingSubscriptionRepository, times(1)).save(any(PendingSubscription.class));
        verifyZeroInteractions(notifyService);
    }

    @Test(expected = SubscriptionAlreadyExistsException.class)
    public void expectSubscriptionAlreadyExistsExceptionWhenActiveSubscriptionAlreadyExists() throws Exception {

        Optional<Subscription> existingSubscription = Optional.of(new Subscription());
        withExpectedSubscription(existingSubscription);
        LocalDate date = LocalDate.now();

        subscriptionService.createPendingSubscription(TEST_VRM, EMAIL, date);
    }

    private void withExpectedSubscription(Optional<Subscription> subscription) {
        when(subscriptionRepository.findByVrmAndEmail(TEST_VRM, EMAIL)).thenReturn(subscription);
    }

}
