package org.acme.saas.service;

import io.smallrye.common.annotation.Blocking;
import org.acme.saas.restclient.CostComputationBody;
import org.acme.saas.restclient.CostComputationResponse;
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
    public double calculatePrice(String tier, int avgConcurrentShoppers) {
        CostComputationResponse price =
                rulesClient.costComputation(CostComputationBody.builder().tier(tier).avgAverageConcurrentShoppers(avgConcurrentShoppers).build());
        log.infof("Calculated price for %s/%d is: %s", tier, avgConcurrentShoppers, price);
        if (price.getCalculatedPrice() != null) {
            return price.getCalculatedPrice().doubleValue();
        }

        throw new NotFoundException();
    }

    @Blocking
    public int[] calculateInstanceCount(int avgConcurrentShoppers, int peakConcurrentShoppers) {
        ProvisionPlanResponse provisionPlan =
                rulesClient.provisionPlan(ProvisionPlanBody.builder().avgConcurrentShoppers(avgConcurrentShoppers).peakConcurrentShoppers(peakConcurrentShoppers).build());
        log.infof("Calculated replicas for %d/%d is: %s", avgConcurrentShoppers, peakConcurrentShoppers, provisionPlan);
        return new int[]{provisionPlan.getReplicas().getMinReplicas(), provisionPlan.getReplicas().getMaxReplicas()};
    }
}
