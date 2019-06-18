package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfirmationCodeGeneratorTests {

    @Test
    public void pseudoRandomConfirmationCodeIsFiveCharactersLong() {

        String id = ConfirmationCodeGenerator.generateCode();
        assertEquals(5, id.length());
    }
}
