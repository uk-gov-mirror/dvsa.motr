package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.eventlog.EventLogger;
import uk.gov.dvsa.motr.web.component.subscription.model.Subscription;
import uk.gov.dvsa.motr.web.component.subscription.persistence.SubscriptionRepository;
import uk.gov.dvsa.motr.web.eventlog.unsubscribe.UnsubscribeEvent;
import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.UnsubscribeViewModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static uk.gov.dvsa.motr.web.resource.RedirectResponseBuilder.redirect;

@Singleton
@Path("/unsubscribe")
@Produces("text/html")
public class UnsubscribeResource {

    private SubscriptionRepository subscriptionRepository;
    private TemplateEngine renderer;

    @Inject
    public UnsubscribeResource(SubscriptionRepository subscriptionRepository, TemplateEngine renderer) {

        this.subscriptionRepository = subscriptionRepository;
        this.renderer = renderer;
    }

    @GET
    @Path("{id}")
    public String unsubscribeGet(@PathParam("id") String id) throws Exception {

        Subscription subscription = this.subscriptionRepository.findById(id).orElseThrow(NotFoundException::new);

        UnsubscribeViewModel viewModel = populateViewModelFromSubscription(subscription);
        Map<String, Object> map = new HashMap<>();
        map.put("viewModel", viewModel);
        return renderer.render("unsubscribe", map);
    }

    @POST
    @Path("{id}")
    public Response unsubscribePost(@PathParam("id") String id) throws Exception {

        Subscription subscription = this.subscriptionRepository.findById(id).orElseThrow(NotFoundException::new);
        this.subscriptionRepository.delete(subscription);

        EventLogger.logEvent(new UnsubscribeEvent().setVrm(subscription.getVrm())
                .setEmail(subscription.getEmail()).setExpiryDate(subscription.getMotDueDate()));

        String uri = UriBuilder.fromUri("unsubscribe/confirmed")
                .queryParam("expiryDate", subscription.getMotDueDate().toString())
                .queryParam("vrm", subscription.getVrm())
                .queryParam("email", subscription.getEmail()).build().toString();

        return redirect(uri);
    }

    @GET
    @Path("confirmed")
    public String unsubscribeConfrimed(
            @QueryParam("expiryDate") String expiryDate,
            @QueryParam("vrm") String vrm,
            @QueryParam("email") String email
    ) throws Exception {

        UnsubscribeViewModel viewModel = new UnsubscribeViewModel()
                .setRegistration(vrm)
                .setExpiryDate(LocalDate.parse(expiryDate))
                .setEmail(email);


        Map<String, Object> map = new HashMap<>();
        map.put("viewModel", viewModel);
        return renderer.render("unsubscribe-confirmation", map);
    }

    private UnsubscribeViewModel populateViewModelFromSubscription(Subscription subscription) {

        return new UnsubscribeViewModel()
                .setEmail(subscription.getEmail())
                .setExpiryDate(subscription.getMotDueDate())
                .setRegistration(subscription.getVrm());
    }
}
