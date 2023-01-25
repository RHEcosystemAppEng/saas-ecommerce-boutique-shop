package org.acme.saas.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantData {
    private String tenantKey;
    private String tenantName;
    private String tier;
    private String avgConcurrentShoppers;
    private String peakConcurrentShoppers;
    private String status;
}
