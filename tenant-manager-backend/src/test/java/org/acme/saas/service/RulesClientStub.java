package org.acme.saas.service;

import org.acme.saas.restclient.CostComputationBody;
import org.acme.saas.restclient.CostComputationResponse;
import org.acme.saas.restclient.ProvisionPlanBody;
import org.acme.saas.restclient.ProvisionPlanResponse;
import org.acme.saas.restclient.RulesClient;
import org.mockito.Mockito;

import javax.ws.rs.NotFoundException;

import static org.mockito.ArgumentMatchers.any;

public class RulesClientStub {
    public static void initMock(RulesClient mockRestClient) {
        Mockito.when(mockRestClient.costComputation(any())).thenAnswer(i -> {
            CostComputationBody costComputationBody = (CostComputationBody) i.getArguments()[0];
            double price = RulesClientStub.calculatePrice(costComputationBody.getTier(),
                    costComputationBody.getAvgAverageConcurrentShoppers());
            return CostComputationResponse.builder().calculatedPrice(price).build();
        });
        Mockito.when(mockRestClient.provisionPlan(any())).thenAnswer(i -> {
            ProvisionPlanBody provisionPlanBody = (ProvisionPlanBody) i.getArguments()[0];
            int[] replicas = RulesClientStub.calculateInstanceCount(provisionPlanBody.getAvgConcurrentShoppers(),
                    provisionPlanBody.getPeakConcurrentShoppers());
            return ProvisionPlanResponse.builder().replicas(ProvisionPlanResponse.ComputedReplicas.builder().minReplicas(replicas[0]).maxReplicas(replicas[1]).build()).build();
        });
    }

    static double calculatePrice(String tier, int avgConcurrentShoppers) {
        return switch (tier) {
            case "Free" -> 0.0;
            case "Silver" -> (avgConcurrentShoppers / 100.0) * 10;
            case "Gold" -> (avgConcurrentShoppers / 100.0) * 20;
            case "Premium" -> (avgConcurrentShoppers / 100.0) * 30;
            default -> throw new NotFoundException();
        };
    }

    static int[] calculateInstanceCount(int avgConcurrentShoppers, int peakConcurrentShoppers) {
        int min = avgConcurrentShoppers / 50;
        int max = peakConcurrentShoppers / 50;

        return new int[]{min, max};
    }
}
