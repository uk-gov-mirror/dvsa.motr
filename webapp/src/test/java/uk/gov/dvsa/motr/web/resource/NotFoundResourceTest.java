package uk.gov.dvsa.motr.web.resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.NotFoundException;

public class NotFoundResourceTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test()
    public void testNotFoundExceptionThrown() throws Exception {

        exception.expect(NotFoundException.class);
        new NotFoundResource().notFound();
    }
}
