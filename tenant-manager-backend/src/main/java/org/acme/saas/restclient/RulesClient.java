package org.acme.saas.restclient;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.data.CostComputationRequest;
import org.acme.saas.model.data.CostComputationResponse;
import org.acme.saas.model.data.HpaComputationRequest;
import org.acme.saas.model.data.HpaComputationResponse;
import org.acme.saas.model.data.ProvisionRequest;
import org.acme.saas.model.data.ProvisionResponse;
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
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 3, delay = 500)
    @Fallback(ProvisionPlanFallback.class)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 5000)
    Uni<ProvisionResponse> updateTierProvisionPlan(ProvisionRequest provisionPlanBody);

    public static class ProvisionPlanFallback implements FallbackHandler<Uni<ProvisionResponse>> {

        @Override
        public Uni<ProvisionResponse> handle(ExecutionContext context) {
            log.warnf("Running ProvisionPlanFallback due to %s", context.getFailure().getMessage());
            return Uni.createFrom().item(
                    () -> null);
        }

    }

    @POST
    @Path("CostComputation")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 3, delay = 500)
    @Fallback(CostComputationFallback.class)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 5000)
    Uni<CostComputationResponse> calculatePrice(CostComputationRequest costComputationBody);


    public static class CostComputationFallback implements FallbackHandler<Uni<CostComputationResponse>> {

        @Override
        public Uni<CostComputationResponse> handle(ExecutionContext context) {
            log.warnf("Running CostComputationFallback due to %s", context.getFailure().getMessage());
            return Uni.createFrom().item(
                    () -> null);
        }

    }
    @POST
    @Path("HpaComputation")
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 3, delay = 500)
    @Fallback(HpaComputationFallback.class)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 5000)
    Uni<HpaComputationResponse> calculateHpa(HpaComputationRequest hpaComputationRequest);


    public static class HpaComputationFallback implements FallbackHandler<Uni<HpaComputationResponse>> {

        @Override
        public Uni<HpaComputationResponse> handle(ExecutionContext context) {
            log.warnf("Running HpaComputationFallback due to %s", context.getFailure().getMessage());
            return Uni.createFrom().item(
                    () -> null);
        }

    }
}
