package uk.gov.dvsa.motr.web.resource;

import uk.gov.dvsa.motr.web.render.TemplateEngine;
import uk.gov.dvsa.motr.web.viewmodel.ReviewViewModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static java.util.Collections.emptyMap;

@Singleton
@Path("/review")
@Produces("text/html")
public class ReviewResource {

    private final TemplateEngine renderer;

    @Inject
    public ReviewResource(TemplateEngine renderer) {
        this.renderer = renderer;
    }

    @GET
    public String reviewPage() throws Exception {

        Map<String, Object> map = new HashMap<>();
        ReviewViewModel viewModel = new ReviewViewModel();

        //TODO add in call to get the vehicle from MOTH

        //TODO replace this dummy data with the information from MOTH and info from cookie
        viewModel.setColour("Black")
                .setEmail("test@test.com")
                .setExpiryDate(LocalDate.of(2017, 2, 2))
                .setMakeModel("Ford Fiesta")
                .setRegistration("test-reg")
                .setYearOfManufacture("2007");

        map.put("viewModel", viewModel);

        return renderer.render("review", map);
    }

    @POST
    public String reviewPagePost() throws Exception {

        //TODO Add in validation of both VRM and EMAIL formats
        //TODO Add in call to dynamo DB to persist subscription
        //TODO Add in call to gov notify to set up the subscription.
        return renderer.render("review", emptyMap());
    }
}
