package uk.gov.dvsa.motr.web.viewmodel;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

public class SubscriptionConfirmationViewModelTest {

    private SubscriptionConfirmationViewModel viewModel;

    @Before
    public void setUp() {

        this.viewModel = new SubscriptionConfirmationViewModel();
    }

    @Test
    public void whenExpiryDateIsSetCorrectFormatIsReturned() {

        this.viewModel.setExpiryDate(LocalDate.of(2017, 3, 10));
        assertTrue(this.viewModel.getExpiryDate().equals("10 March 2017"));
    }

    @Test
    public void whenEmailIsSetItCanBeRetrieved() {

        String expected = "test@test.com";
        this.viewModel.setEmail(expected);
        assertTrue(expected.equals(this.viewModel.getEmail()));
    }

    @Test
    public void whenRegistrationIsSetItCanBeRetrieved() {

        String expected = "test-reg";
        this.viewModel.setVrm(expected);
        assertTrue(expected.equals(this.viewModel.getVrm()));
    }
}
