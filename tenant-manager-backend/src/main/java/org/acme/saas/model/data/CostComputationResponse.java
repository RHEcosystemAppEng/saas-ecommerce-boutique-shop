package org.acme.saas.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CostComputationResponse {
    String costPerHundredUnits;
    String tier;
    int averageConcurrentShoppers;
    double calculatedPrice;

}
