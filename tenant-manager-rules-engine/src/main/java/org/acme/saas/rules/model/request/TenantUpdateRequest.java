package org.acme.saas.rules.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantUpdateRequest {
    private String tenant;
    private long averageConcurrentShoppers;
}
