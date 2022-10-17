package org.acme.saas.restclient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.json.bind.annotation.JsonbProperty;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvisionPlanResponse {
    @JsonbProperty("Compute Replicas")
    ComputedReplicas replicas;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComputedReplicas {
        int minReplicas;
        int maxReplicas;
    }
}