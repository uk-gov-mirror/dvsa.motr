package uk.gov.dvsa.motr.web.viewmodel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EmailConfirmationPendingViewModelTest {

    private EmailConfirmationPendingViewModel viewModel;

    @Before
    public void setUp() {

        this.viewModel = new EmailConfirmationPendingViewModel();
    }

    @Test
    public void whenEmptyEmailTheDisplayStringIsEmpty() {

        this.viewModel.setEmail("");
        assertTrue(this.viewModel.getEmailDisplayString().equals(""));
    }

    @Test
    public void whenEmailNotEmptyTheDisplayStringIsEmailPlusSpace() {

        this.viewModel.setEmail("test@test.com");
        assertTrue(this.viewModel.getEmailDisplayString().equals("test@test.com "));
    }
}
