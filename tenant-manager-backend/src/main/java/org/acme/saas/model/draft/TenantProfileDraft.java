package org.acme.saas.model.draft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TenantProfileDraft {

    private String tenantKey;
    private String email;
    private String password;
    private String tenantName;
    private String orgName;
    private String address;
    private String contactName;
    private String phone;
    private String status;

}
