package org.acme.saas.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.acme.saas.model.Request;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ProvisionRequest {

    List<TierRequest> tierRequests = new ArrayList<>();

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class TierRequest {
        String tier;
        String newTotalAverageConcurrentShoppers;
        List<TenantRequest> tenantRequests = new ArrayList<>();

    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class TenantRequest {
        String tenant;
        String averageConcurrentShoppers;
    }
}
