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
public class ProvisionPlanBody {
    @JsonbProperty("Average Concurrent Shoppers")
    int avgConcurrentShoppers;
    @JsonbProperty("Peak Concurrent Shoppers")
    int peakConcurrentShoppers;
}