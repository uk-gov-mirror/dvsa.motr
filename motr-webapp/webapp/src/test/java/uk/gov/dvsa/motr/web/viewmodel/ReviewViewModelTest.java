package uk.gov.dvsa.motr.web.viewmodel;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.gov.dvsa.motr.vehicledetails.VehicleType;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(DataProviderRunner.class)
public class ReviewViewModelTest {

    private static String UNKNOWN_STRING = "UNKNOWN";
    private static String UNAVAILABLE_STRING = "Unavailable";
    private static String ANNUAL_EXPIRE_LABEL = "Annual test expiry date";
    private static String MOT_EXPIRE_LABEL = "MOT expiry date";
    private static String ANNUAL_DUE_LABEL = "Annual test due date";
    private static String MOT_DUE_LABEL = "MOT due date";

    @Test
    public void whenColourIsNullItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour(null, null);
        assertEquals(UNKNOWN_STRING, viewModel.getColour());
    }

    @Test
    public void whenColourIsEmptyItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour("", null);
        assertEquals(UNKNOWN_STRING, viewModel.getColour());
    }

    @Test
    public void whenColourIsEmptyForTrailerItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setVehicleType(VehicleType.TRAILER);
        viewModel.setColour("", null);
        assertEquals(UNAVAILABLE_STRING, viewModel.getColour());
    }

    @Test
    public void colourIsUpperCasedWhenSetForMotVehicle() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setVehicleType(VehicleType.MOT);
        viewModel.setColour("black", "");
        assertEquals("BLACK", viewModel.getColour());
    }

    @Test
    public void whenColourSetForHgvVehicleItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setVehicleType(VehicleType.HGV);
        viewModel.setColour("black", "");
        assertEquals(UNKNOWN_STRING, viewModel.getColour());
    }

    @Test
    public void whenVehicleTypeIsNotSetColorIsVisible() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setColour("black", "");
        assertEquals("BLACK", viewModel.getColour());
    }

    @Test
    public void whenColourSetForPsvVehicleItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setVehicleType(VehicleType.PSV);
        viewModel.setColour("black", "red");
        assertEquals(UNKNOWN_STRING, viewModel.getColour());
    }

    @Test
    public void colourAndSecondaryColourIsUpperCasedWhenSetForMotVehicle() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setVehicleType(VehicleType.MOT);
        viewModel.setColour("black", "blue");
        assertEquals("BLACK, BLUE", viewModel.getColour());
    }

    @Test
    public void whenYearOfManufactureIsNullItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setYearOfManufacture(null);
        assertEquals(UNKNOWN_STRING, viewModel.getYearOfManufacture());
    }

    @Test
    public void whenYearOfManufactureIsEmptyItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setYearOfManufacture("");
        assertEquals(UNKNOWN_STRING, viewModel.getYearOfManufacture());
    }

    @Test
    public void whenExpiryDateIsNullItIsSetToUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setExpiryDate(null);
        assertTrue(viewModel.getExpiryDate().equals(UNKNOWN_STRING));
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
        viewModel.setContact(expected);
        assertTrue(expected.equals(viewModel.getContact()));
    }

    @Test
    public void whenPhoneNumberIsSetItCanBeRetrieved() {

        String expected = "07806754189";
        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setContact(expected);
        assertEquals(expected, viewModel.getContact());
    }

    @Test
    public void whenRegistrationIsSetItCanBeRetrieved() {

        String expected = "test-reg";
        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setRegistration(expected);
        assertTrue(expected.equals(viewModel.getRegistration()));
    }

    @Test
    public void makeIsUpperCasedWhenSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake("Honda");
        assertEquals("HONDA", viewModel.getMake());
    }

    @Test
    public void modelIsUpperCasedWhenSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setModel("Civic");
        assertEquals("CIVIC", viewModel.getModel());
    }

    @Test
    public void modelIsUnknownWhenNotSet() {

        ReviewViewModel viewModel = new ReviewViewModel();
        assertEquals(UNKNOWN_STRING, viewModel.getModel());
    }

    @Test
    public void whenMakeIsNullAndMakeInFullIsSet_MakeIsReturnedAsMakeInFull() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake(null);
        viewModel.setMakeInFull("Ford Focus 1.8 Tdi");
        assertEquals("FORD FOCUS 1.8 TDI", viewModel.getMakeInfull());
    }

    @Test
    public void whenMakeIsNullAndModelIsSetAndMakeInFullIsSet_ModelIsReturned() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake(null);
        viewModel.setMakeInFull("Ford Focus 1.8 TDI");
        viewModel.setModel("Focus");
        assertEquals("FOCUS", viewModel.getModel());
    }

    @Test
    public void whenMakeAndMakeInFullAreNull_MakeIsReturnedAsUnknown() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake(null);
        viewModel.setMakeInFull(null);
        assertEquals(UNKNOWN_STRING, viewModel.getMake());
    }

    @Test
    public void whenMakeIsSetAndMakeInFullIsSet_MakeIsReturned() {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setMake("Ford");
        viewModel.setMakeInFull("Ford Focus");
        assertEquals("FORD", viewModel.getMake());
    }

    @Test
    @UseDataProvider("dataProviderExpiryDateLabel")
    public void whenToggleTypeAndHasTestsAreSet_AppropriateLabelIsReturned(
            boolean hgvPsvToggle,
            VehicleType vehicleType,
            boolean hasTests,
            String expectedLabel) {

        ReviewViewModel viewModel = new ReviewViewModel();
        viewModel.setHgvPsvToggle(hgvPsvToggle);
        viewModel.setVehicleType(vehicleType);
        viewModel.setHasTests(hasTests);
        viewModel.setDvlaVehicle(!hasTests);
        assertEquals(expectedLabel, viewModel.getExpiryDateLabelText());
    }

    @DataProvider
    public static Object[][] dataProviderExpiryDateLabel() throws IOException {

        return new Object[][]{
                {true, VehicleType.HGV, true, ANNUAL_EXPIRE_LABEL},
                {true, VehicleType.PSV, true, ANNUAL_EXPIRE_LABEL},
                {true, VehicleType.MOT, true, MOT_EXPIRE_LABEL},
                {false, VehicleType.HGV, true, MOT_EXPIRE_LABEL},
                {false, VehicleType.MOT, true, MOT_EXPIRE_LABEL},
                {true, VehicleType.HGV, false, ANNUAL_DUE_LABEL},
                {true, VehicleType.PSV, false, ANNUAL_DUE_LABEL},
                {true, VehicleType.MOT, false, MOT_DUE_LABEL},
                {false, VehicleType.HGV, false, MOT_DUE_LABEL},
                {false, VehicleType.MOT, false, MOT_DUE_LABEL}
        };
    }
}
