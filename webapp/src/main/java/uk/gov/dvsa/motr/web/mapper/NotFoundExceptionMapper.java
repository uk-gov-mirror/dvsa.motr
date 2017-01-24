package uk.gov.dvsa.motr.web.mapper;

import uk.gov.dvsa.motr.web.render.TemplateEngine;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static java.util.Collections.emptyMap;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * Executes when no route has been matched
 * or when 404 status has been explicitly requested by throwing {@link NotFoundException}
 * Produces tailored HTML version of 404 error page.
 * When not provided, JSON-like structure will be returned.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    private TemplateEngine renderer;

    @Inject
    public NotFoundExceptionMapper(TemplateEngine renderer) {
        this.renderer = renderer;
    }

    @Override
    public Response toResponse(NotFoundException exception) {

        String content = renderer.render("error/not-found", emptyMap());
        return Response.status(NOT_FOUND)
                .header("Content-type", "text/html")
                .entity(content)
                .build();
    }
}
