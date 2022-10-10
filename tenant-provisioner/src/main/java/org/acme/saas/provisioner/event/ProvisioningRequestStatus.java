package org.acme.saas.provisioner.event;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
public class ProvisioningRequestStatus {
    public final static String EVENT_TYPE = "org.acme.saas.provisioner.ProvisioningStatus";
    private Long tenandId;
    private String tenantName;
    private Status status;
    public enum Status {
        Initiated,
        Completed,
        Failed
    }
}
