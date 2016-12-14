package uk.gov.dvsa.motr.web.test.render;

import uk.gov.dvsa.motr.web.render.TemplateEngine;

public class TemplateEngineStub implements TemplateEngine {

    public static final String RESPONSE = "rendered";

    private String template;

    private Object context;

    public String getTemplate() {
        return this.template;
    }

    public <T> T getContext(Class<T> clazz) {
        return clazz.cast(context);
    }

    @Override
    public String render(String templateName, Object context) {
        this.template = templateName;
        this.context = context;

        return RESPONSE;
    }

}
