package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.EmailConfirmationPendingViewModel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Singleton
@Path("/email-confirmation-pending")
@Produces("text/html")
public class EmailConfirmationPendingResource {

    private final TemplateEngine renderer;
    private final MotrSession motrSession;

    @Inject
    public EmailConfirmationPendingResource(
            TemplateEngine renderer,
            MotrSession motrSession
    ) {

        this.renderer = renderer;
        this.motrSession = motrSession;
    }

    @GET
    public String confirmEmailGet() {

        EmailConfirmationPendingViewModel viewModel = new EmailConfirmationPendingViewModel();

        viewModel.setEmail(motrSession.getEmailFromSession());

        Map<String, Object> map = new HashMap<>();
        map.put("viewModel", viewModel);

        motrSession.setShouldClearCookies(true);
        return renderer.render("email-confirmation-pending", map);
    }
}
