package uk.gov.dvsa.motr.web.validator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChannelSelectionValidatorTest {

    private static final String EMPTY_CHANNEL_SELECTION_MESSAGE = "Choose what type of reminder you want to receive";
    private static final String INVALID_CHANNEL_SELECTION_MESSAGE = "Choose what type of reminder you want to receive";

    private ChannelSelectionValidator validator;

    @Before
    public void setUp() {

        validator = new ChannelSelectionValidator();
    }

    @Test
    public void emptyChannelSelectionIsInvalid() {

        assertFalse(validator.isValid(""));
        assertEquals(EMPTY_CHANNEL_SELECTION_MESSAGE, validator.getMessage());
    }

    @Test
    public void nullChannelSelectionIsInvalid() {

        assertFalse(validator.isValid(null));
        assertEquals(EMPTY_CHANNEL_SELECTION_MESSAGE, validator.getMessage());
    }

    @Test
    public void invalidChannelSelectionIsInvalid() {

        assertFalse(validator.isValid("post"));
        assertEquals(INVALID_CHANNEL_SELECTION_MESSAGE, validator.getMessage());
    }

    @Test
    public void emailChannelSelectionIsValid() {

        assertTrue(validator.isValid("email"));
    }

    @Test
    public void textChannelSelectionIsValid() {

        assertTrue(validator.isValid("text"));
    }
}
