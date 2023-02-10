package org.acme.saas.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HpaComputationResponse {
    int averageConcurrentShoppers;
    int divisor;
    String bucket;
    String tier;
    String minReplicas;
    String maxReplicas;
    HpaReplica hpaReplicas;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class HpaReplica {
        int maxReplicas;
        int minReplicas;
        double replicas;
        String comment;
    }
}
