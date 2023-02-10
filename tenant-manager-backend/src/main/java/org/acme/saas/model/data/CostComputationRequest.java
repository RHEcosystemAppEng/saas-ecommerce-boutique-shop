package org.acme.saas.model.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CostComputationRequest {
    String tier;
    String averageConcurrentShoppers;
}
