package uk.gov.dvsa.motr.web.resource;

import org.junit.Test;

import uk.gov.dvsa.motr.web.test.render.TemplateEngineStub;

import static org.junit.Assert.assertEquals;

import static uk.gov.dvsa.motr.web.test.render.TemplateEngineStub.RESPONSE;

public class PrivacyPolicyTest {
    
    @Test
    public void privacyTemplateIsRenderedWhenPrivacyPolicyPathAccessed() throws Exception {

        TemplateEngineStub engine = new TemplateEngineStub();
        PrivacyPolicyResource resource = new PrivacyPolicyResource(engine);

        assertEquals(RESPONSE, resource.privacyPage());
        assertEquals("privacy-policy", engine.getTemplate());
    }
}
