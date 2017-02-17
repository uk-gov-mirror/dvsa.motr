package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class HomepageResourceTest {

    @Test
    public void homepageTemplateIsRenderedWhenRootPathAccessed() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        HomepageResource resource = new HomepageResource("testUrl/", new MotrSession(), engine);

        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("vrm_url", "testUrl/vrm");
        assertEquals(RESPONSE, resource.homePage());
        assertEquals("home", engine.getTemplate());
        assertEquals(expectedMap.toString(), engine.getContext(Map.class).toString());
    }
}
