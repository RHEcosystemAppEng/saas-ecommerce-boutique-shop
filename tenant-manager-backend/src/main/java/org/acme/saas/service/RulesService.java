package org.acme.saas.service;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import org.acme.saas.restclient.CostComputationBody;
import org.acme.saas.restclient.ProvisionPlanBody;
import org.acme.saas.restclient.ProvisionPlanResponse;
import org.acme.saas.restclient.RulesClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class RulesService {
    private Logger log = Logger.getLogger(RulesService.class);

    @RestClient
    RulesClient rulesClient;

    @Blocking
    public Uni<Double> calculatePrice(String tier, int avgConcurrentShoppers) {
        return
                rulesClient.costComputation(CostComputationBody.builder().tier(tier).avgAverageConcurrentShoppers(avgConcurrentShoppers).build()).
                        onItem().transformToUni(costComputationResponse -> {
                            Double price = costComputationResponse.getCalculatedPrice();
                            log.infof("Calculated price for %s/%d is: %s", tier, avgConcurrentShoppers, price);
                            return Uni.createFrom().item(price);
                        });
    }

    @Blocking
    public Uni<int[]> calculateInstanceCount(int avgConcurrentShoppers, int peakConcurrentShoppers) {
        return
                rulesClient.provisionPlan(ProvisionPlanBody.builder().avgConcurrentShoppers(avgConcurrentShoppers).peakConcurrentShoppers(peakConcurrentShoppers).build()).
                        onItem().ifNotNull().transformToUni(provisionPlanResponse -> {
                            log.infof("Calculated replicas for %d/%d is: %s", avgConcurrentShoppers,
                                    peakConcurrentShoppers, provisionPlanResponse);
                            ProvisionPlanResponse.ComputedReplicas replicas = provisionPlanResponse.getReplicas();
                            return Uni.createFrom().item(new int[]{replicas.getMinReplicas(),
                                    replicas.getMaxReplicas()});
                        }).onItem().ifNull().failWith(NotFoundException::new);
    }
}
