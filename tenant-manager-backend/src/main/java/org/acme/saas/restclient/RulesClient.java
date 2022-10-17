package org.acme.saas.restclient;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@ApplicationScoped
@RegisterRestClient
public interface RulesClient {
    static final Logger log = Logger.getLogger(RulesClient.class);

    @POST
    @Path("/CostComputation")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 3, delay = 2000)
    @Fallback(CostComputationFallback.class)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 5000)
    Uni<CostComputationResponse> costComputation(CostComputationBody costComputationBody);

    @POST
    @Path("/ProvisionPlan")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 3, delay = 500)
    @Fallback(ProvisionPlanFallback.class)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 5000)
    Uni<ProvisionPlanResponse> provisionPlan(ProvisionPlanBody provisionPlanBody);

    public static class CostComputationFallback implements FallbackHandler<Uni<CostComputationResponse>> {

        @Override
        public Uni<CostComputationResponse> handle(ExecutionContext context) {
            log.warnf("Running CostComputationFallback due to %s", context.getFailure().getMessage());
            return Uni.createFrom().item(CostComputationResponse.builder().calculatedPrice(0.0).build());
        }

    }

    public static class ProvisionPlanFallback implements FallbackHandler<Uni<ProvisionPlanResponse>> {

        @Override
        public Uni<ProvisionPlanResponse> handle(ExecutionContext context) {
            log.warnf("Running ProvisionPlanFallback due to %s", context.getFailure().getMessage());
            return Uni.createFrom().item(ProvisionPlanResponse.builder().replicas(ProvisionPlanResponse.ComputedReplicas.builder().minReplicas(0).maxReplicas(0).build()).build());
        }

    }
}
