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
public class ProvisionResponse {

    List<ResourceByTier> resourcesByTier;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ResourceByTier {
        String tier;
        List<Resource> resourcesByMicroservices;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Resource {
        String bucket;
        String tenant;
        String namespace;
        String microservice;
        HpaResources hpaResources;
    }
    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class HpaResources {
        int minReplicas;
        int maxReplicas;
        double replicas;
        String comment;
    }
}
