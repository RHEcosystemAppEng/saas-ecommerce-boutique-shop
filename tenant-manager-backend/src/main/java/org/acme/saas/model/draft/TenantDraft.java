package org.acme.saas.model.draft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TenantDraft {

    private String tenantKey;
    private String email;
    private String password;
    private String tenantName;
    private String orgName;
    private String address;
    private String contactName;
    private String phone;
    private List<SubscriptionDraft> subscriptions;
    private String status;

}
