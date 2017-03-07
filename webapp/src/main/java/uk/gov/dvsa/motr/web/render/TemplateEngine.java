package uk.gov.dvsa.motr.web.render;

/**
 * Generic interface for template engine implementations.
 */
public interface TemplateEngine {

    /**
     * Renders a templateName with parameters
     *
     * @param templateName templateName name
     * @param context      context representation e.g. a key-value map
     * @return rendered content
     */
    String render(String templateName, Object context);
}
