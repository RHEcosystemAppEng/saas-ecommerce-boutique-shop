package org.acme.saas.rules.model.request;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TierUpdateRequest {
    private String tier;
    private long newTotalAverageConcurrentShoppers;
    private List<TenantUpdateRequest> tenantRequests = new ArrayList<>();
}
