package org.acme.saas.model.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HpaComputationRequest {
    String tier;
    String bucket;
    int averageConcurrentShoppers;
}
