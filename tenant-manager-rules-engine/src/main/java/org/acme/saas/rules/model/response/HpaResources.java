package org.acme.saas.rules.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HpaResources {
    private long minReplicas;
    private long maxReplicas;
    private double replicas;
    private String comment;
}