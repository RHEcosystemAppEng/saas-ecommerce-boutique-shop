package org.acme.saas.service;

import org.jboss.logging.Logger;

public class RulesService {
    private Logger log = Logger.getLogger(RulesService.class);

//    @Blocking
//    public Uni<Double> calculatePrice(String tier, int avgConcurrentShoppers) {
//        return
//                rulesClient.costComputation(CostComputationBody.builder().tier(tier).avgAverageConcurrentShoppers(avgConcurrentShoppers).build()).
//                        onItem().transformToUni(costComputationResponse -> {
//                            Double price = costComputationResponse.getCalculatedPrice();
//                            log.infof("Calculated price for %s/%d is: %s", tier, avgConcurrentShoppers, price);
//                            return Uni.createFrom().item(price);
//                        });
//    }


}
