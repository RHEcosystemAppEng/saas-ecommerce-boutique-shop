package org.acme.saas.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestChangeData {

    private String tenantKey;
    private String TenantName;
    private String currentTier;
    private String newTier;
    private String serviceName;
    private String oldMinInstances;
    private String newMinInstances;
    private String oldMaxInstances;
    private String newMaxInstances;
}
