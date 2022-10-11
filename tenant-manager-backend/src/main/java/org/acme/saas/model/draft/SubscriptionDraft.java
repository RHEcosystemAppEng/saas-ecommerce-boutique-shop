package org.acme.saas.model.draft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
