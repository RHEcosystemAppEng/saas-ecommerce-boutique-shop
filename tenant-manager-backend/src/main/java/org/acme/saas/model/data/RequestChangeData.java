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

    private long requestId;
    private String tenantKey;
    private String tenantName;
    private String currentTier;
    private String newTier;
    private String serviceName;
    private int oldMinInstances;
    private int newMinInstances;
    private int oldMaxInstances;
    private int newMaxInstances;
}
