package org.acme.saas.restclient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbProperty;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostComputationBody {
    @JsonbProperty("Tier")
    String tier;
    @JsonbProperty("Average Concurrent Shoppers")
    int avgAverageConcurrentShoppers;
}