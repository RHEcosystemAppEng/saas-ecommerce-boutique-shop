package org.acme.saas.model.draft;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestDraft {

    @NonNull
    private String tenantKey;
    private String hostName;
    private String tier;
    private int avgConcurrentShoppers;
    private int peakConcurrentShoppers;
    private String fromTime;
    private String toTime;
    private String status;
}
