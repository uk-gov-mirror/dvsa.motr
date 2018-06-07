package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;
import uk.gov.dvsa.motr.web.component.subscription.model.CancelledSubscription;
import uk.gov.dvsa.motr.web.component.subscription.model.ContactDetail;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.CancelledSubscriptionRepository;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;

import java.time.LocalDate;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Optional.empty;

public class UnsubscribeServiceTest {

    private static final String VRM = "vrm";
    private static final String EMAIL = "email";
    private static final LocalDate DATE = LocalDate.now();
    private static final String UNSUBSCRIBE_ID = "asdasdasd";

    private final SubscriptionRepository subscriptionRepository = mock(SubscriptionRepository.class);
    private final CancelledSubscriptionRepository cancelledSubscriptionRepository = mock(CancelledSubscriptionRepository.class);

    private UnsubscribeService unsubscriptionService;

    @Before
    public void setUp() {

        this.unsubscriptionService = new UnsubscribeService(
                subscriptionRepository,
                cancelledSubscriptionRepository
        );
    }

    @Test
    public void unsubscribeWhenSubscriptionDoesExist() throws Exception {
        ArgumentCaptor<CancelledSubscription> cancelledSubscriptionArgumentCaptor = ArgumentCaptor.forClass(CancelledSubscription.class);

        Subscription subscription = subscriptionStub();
        withExpectedSubscription(Optional.of(subscription));

        this.unsubscriptionService.unsubscribe(UNSUBSCRIBE_ID);

        verify(subscriptionRepository, times(1)).findByUnsubscribeId(UNSUBSCRIBE_ID);
        verify(cancelledSubscriptionRepository, times(1)).save(cancelledSubscriptionArgumentCaptor.capture());

        assertEquals(subscription.getVrm(), cancelledSubscriptionArgumentCaptor.getValue().getVrm());
        assertEquals(subscription.getVehicleType(), cancelledSubscriptionArgumentCaptor.getValue().getVehicleType());
        assertEquals(subscription.getContactDetail().getValue(),
                cancelledSubscriptionArgumentCaptor.getValue().getContactDetail().getValue());
        assertEquals(subscription.getContactDetail().getContactType(),
                cancelledSubscriptionArgumentCaptor.getValue().getContactDetail().getContactType());
        assertEquals(subscription.getUnsubscribeId(), cancelledSubscriptionArgumentCaptor.getValue().getUnsubscribeId());
    }

    @Test(expected = NotFoundException.class)
    public void unsubscribeWhenSubscriptionDoesNotExist() throws Exception {

        withExpectedSubscription(empty());

        this.unsubscriptionService.unsubscribe(UNSUBSCRIBE_ID);

        verify(subscriptionRepository, times(0)).findByUnsubscribeId(UNSUBSCRIBE_ID);
        verify(cancelledSubscriptionRepository, times(0)).save(any(CancelledSubscription.class));
    }

    private void withExpectedSubscription(Optional<Subscription> subscription) {
        when(subscriptionRepository.findByUnsubscribeId(UNSUBSCRIBE_ID)).thenReturn(subscription);
    }

    private Subscription subscriptionStub() {

        return new Subscription()
                .setUnsubscribeId(UNSUBSCRIBE_ID)
                .setMotDueDate(DATE)
                .setContactDetail(new ContactDetail(EMAIL, Subscription.ContactType.EMAIL))
                .setVrm(VRM)
                .setVehicleType(VehicleType.HGV);
    }
}
