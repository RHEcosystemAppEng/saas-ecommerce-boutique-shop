package org.acme.saas.model.draft;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SubscriptionDraft {

    @NonNull
    private String tenantKey;
    private String serviceName;
    private String tier;
    private int minInstanceCount;
    private int maxInstanceCount;
    private RequestDraft request;
    private String url;
    private String status;
}
