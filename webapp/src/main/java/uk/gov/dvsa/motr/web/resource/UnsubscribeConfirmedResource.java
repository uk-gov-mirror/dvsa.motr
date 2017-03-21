package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.analytics.DataLayerHelper;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.cookie.MotrSession;
import uk.gov.dvsa.motr.web.cookie.UnsubscribeConfirmationParams;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static uk.gov.dvsa.motr.web.analytics.DataLayerHelper.VRM_KEY;

@Singleton
@Path("/unsubscribe")
@Produces("text/html")
public class UnsubscribeConfirmedResource {

    private TemplateEngine renderer;
    private MotrSession motrSession;

    @Inject
    public UnsubscribeConfirmedResource(
            TemplateEngine renderer,
            MotrSession motrSession
    ) {

        this.renderer = renderer;
        this.motrSession = motrSession;
    }

    @GET
    @Path("confirmed")
    public String unsubscribeConfirmed() throws Exception {

        UnsubscribeConfirmationParams params = motrSession.getUnsubscribeConfirmationParams();
        if (params == null) {
            throw new NotFoundException();
        }

        UnsubscribeViewModel viewModel = new UnsubscribeViewModel()
                .setRegistration(params.getRegistration())
                .setExpiryDate(LocalDate.parse(params.getExpiryDate()))
                .setEmail(params.getEmail());

        DataLayerHelper helper = new DataLayerHelper();
        helper.putAttribute(VRM_KEY, params.getRegistration());

        Map<String, Object> map = new HashMap<>();;
        map.putAll(helper.formatAttributes());
        map.put("viewModel", viewModel);
        return renderer.render("unsubscribe-confirmation", map);
    }

}
