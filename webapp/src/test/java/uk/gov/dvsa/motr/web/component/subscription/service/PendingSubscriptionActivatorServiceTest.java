package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dvsa.motr.notifications.service.NotifyService;
import uk.gov.dvsa.motr.web.component.subscription.exception.InvalidActivationIdException;
import uk.gov.dvsa.motr.web.component.subscription.model.PendingSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.PendingSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.helper.UnsubscriptionUrlHelper;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PendingSubscriptionActivatorServiceTest {

    private static final PendingSubscriptionRepository PENDING_SUBSCRIPTION_REPOSITORY = mock(PendingSubscriptionRepository.class);
    private static final SubscriptionRepository SUBSCRIPTION_REPOSITORY = mock(SubscriptionRepository.class);
    private static final NotifyService NOTIFY_SERVICE = mock(NotifyService.class);
    private static final UnsubscriptionUrlHelper UNSUBSCRIPTION_URL_HELPER = mock(UnsubscriptionUrlHelper.class);
    private static final String PENDING_SUBSCRIPTION_ID = "asdasdasd";
    public static final LocalDate DATE = LocalDate.now();
    public static final String VRM = "vrm";
    public static final String EMAIL = "email";

    private PendingSubscriptionActivatorService subscriptionService;

    @Before
    public void setUp() {

        this.subscriptionService = new PendingSubscriptionActivatorService(
                PENDING_SUBSCRIPTION_REPOSITORY,
                SUBSCRIPTION_REPOSITORY,
                NOTIFY_SERVICE,
                UNSUBSCRIPTION_URL_HELPER
        );
    }

    @Test
    public void saveSubscriptionWhenSubscriptionDoesNotExistCallsDbToSaveDetails() throws Exception {

        PendingSubscription pendingSubscription = new PendingSubscription(PENDING_SUBSCRIPTION_ID)
                .setMotDueDate(DATE)
                .setEmail(EMAIL)
                .setVrm(VRM);

        when(PENDING_SUBSCRIPTION_REPOSITORY.findById(PENDING_SUBSCRIPTION_ID)).thenReturn(Optional.of(pendingSubscription));

        this.subscriptionService.activateSubscription(PENDING_SUBSCRIPTION_ID);

        verify(PENDING_SUBSCRIPTION_REPOSITORY, times(1)).findById(PENDING_SUBSCRIPTION_ID);
        verify(SUBSCRIPTION_REPOSITORY, times(1)).save(any(Subscription.class));
        verify(PENDING_SUBSCRIPTION_REPOSITORY, times(1)).delete(pendingSubscription);
        verify(NOTIFY_SERVICE, times(1)).sendSubscriptionConfirmationEmail(
                eq(pendingSubscription.getEmail()),
                eq(pendingSubscription.getVrm()),
                eq(pendingSubscription.getMotDueDate()),
                anyString()
        );
    }

    @Test(expected = InvalidActivationIdException.class)
    public void throwsExceptionWhenPendingSubscriptionDoesNotExists() throws Exception {

        when(PENDING_SUBSCRIPTION_REPOSITORY.findById(PENDING_SUBSCRIPTION_ID)).thenReturn(Optional.empty());

        this.subscriptionService.activateSubscription(PENDING_SUBSCRIPTION_ID);
        verify(SUBSCRIPTION_REPOSITORY, times(0)).save(any());
        verify(PENDING_SUBSCRIPTION_REPOSITORY, times(0)).delete(any(PendingSubscription.class));
    }
}
