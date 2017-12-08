package uk.gov.dvsa.motr.web.component.subscription.service;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RandomIdGeneratorTests {

    @Test
    public void randomIdIsTwentyTwoCharactersLong() {

        String id = RandomIdGenerator.generateId();
        assertTrue(id.length() == 22);
    }
}
