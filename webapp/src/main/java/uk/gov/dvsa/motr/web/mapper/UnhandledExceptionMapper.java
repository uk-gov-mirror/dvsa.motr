package uk.gov.dvsa.motr.web.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.Collections.emptyMap;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * Executes when no other error handler was executed.
 */
@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = LoggerFactory.getLogger(UnhandledExceptionMapper.class);

    private final TemplateEngine renderer;

    @Inject
    public UnhandledExceptionMapper(TemplateEngine renderer) {
        this.renderer = renderer;
    }

    @Override
    public Response toResponse(Throwable exception) {

        logger.error("Unhandled error!", exception);

        String content = renderer.render("error/internal-error", emptyMap());
        return Response.status(INTERNAL_SERVER_ERROR)
                .header("Content-type", "text/html")
                .entity(content)
                .build();
    }
}
