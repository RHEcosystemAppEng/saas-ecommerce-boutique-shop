package org.acme.saas.provisioner.event;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
public class NewTenantRequest {
    public final static String EVENT_TYPE = "org.acme.saas.provisioner.event.NewTenantRequest";
    private Long tenandId;
    private String tenantName;
}
