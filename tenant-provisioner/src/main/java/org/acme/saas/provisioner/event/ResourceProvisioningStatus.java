package org.acme.saas.provisioner.event;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
public class ResourceProvisioningStatus {
    public final static String EVENT_TYPE = "org.acme.saas.provisioner.ResourceProvisioningStatus";
    private Long tenandId;
    private String resourceName;
    private String resourceType;
    private Status status;
    public enum Status {
        Initiated,
        Completed,
        Failed
    }
}
