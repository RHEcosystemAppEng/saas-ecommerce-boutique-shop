package org.acme.saas.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvisionResponse {

    List<ResourceByTier> resourcesByTier;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResourceByTier {
        String tier;
        List<Resource> resourcesByMicroservices;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
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
    public static class HpaResources {
        int minReplicas;
        int maxReplicas;
        double replicas;
        String comment;
    }
}
