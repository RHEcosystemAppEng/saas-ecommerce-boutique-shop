package org.acme.saas.model.data;

import lombok.Builder;
@Builder
public class TokenData {
    // todo rename it to TenantKey
    String key;
    Long Id;
    String loggedInUserName;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }

    public void setLoggedInUserName(String loggedInUserName) {
        this.loggedInUserName = loggedInUserName;
    }
}
