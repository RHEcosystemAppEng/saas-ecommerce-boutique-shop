package org.acme.saas.rules.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourcesByMicroservice {
    private String bucket;
    private String tenant;
    private String namespace;
    private String microservice;

    private HpaResources hpaResources;
}
