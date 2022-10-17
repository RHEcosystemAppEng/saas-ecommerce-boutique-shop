package org.acme.saas.restclient;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@ApplicationScoped
@RegisterRestClient
public interface RulesClient {

    @POST
    @Path("/CostComputation")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<CostComputationResponse> costComputation(CostComputationBody costComputationBody);

    @POST
    @Path("/ProvisionPlan")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<ProvisionPlanResponse> provisionPlan(ProvisionPlanBody provisionPlanBody);
}
