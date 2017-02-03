package uk.gov.dvsa.motr.web.viewmodel;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

public class ReviewViewModelTest {

    @Test
    public void whenColourIsNullItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour(null);
        assertTrue(viewModel.getColour().equals("Unknown"));
    }

    @Test
    public void whenColourIsEmptyItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour("");
        assertTrue(viewModel.getColour().equals("Unknown"));
    }

    @Test
    public void colourIsUpperCasedWhenSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour("black");
        assertTrue(viewModel.getColour().equals("BLACK"));
    }

    @Test
    public void whenYearOfManufactureIsNullItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setYearOfManufacture(null);
        assertTrue(viewModel.getYearOfManufacture().equals("Unknown"));
    }

    @Test
    public void whenYearOfManufactureIsEmptyItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setYearOfManufacture("");
        assertTrue(viewModel.getYearOfManufacture().equals("Unknown"));
    }

    @Test
    public void whenExpiryDateIsNullItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setExpiryDate(null);
        assertTrue(viewModel.getExpiryDate().equals("Unknown"));
    }

    @Test
    public void whenExpiryDateIsSetCorrectFormatIsReturned() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setExpiryDate(LocalDate.of(2017, 3, 10));
        assertTrue(viewModel.getExpiryDate().equals("10 March 2017"));
    }

    @Test
    public void whenEmailIsSetItCanBeRetrieved() {

        String expected = "test@test.com";
        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setEmail(expected);
        assertTrue(expected.equals(viewModel.getEmail()));
    }

    @Test
    public void whenRegistrationIsSetItCanBeRetrieved() {

        String expected = "test-reg";
        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setRegistration(expected);
        assertTrue(expected.equals(viewModel.getRegistration()));
    }

    @Test
    public void makeModelIsUpperCasedWhenSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMakeModel("Honda Civic");
        assertTrue(viewModel.getMakeModel().equals("HONDA CIVIC"));
    }
}
