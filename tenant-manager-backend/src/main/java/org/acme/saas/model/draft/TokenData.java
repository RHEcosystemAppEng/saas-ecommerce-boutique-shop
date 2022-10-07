package org.acme.saas.model.draft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TokenData{");
        sb.append("key='").append(key).append('\'');
        sb.append(", Id=").append(Id);
        sb.append(", loggedInUserName='").append(loggedInUserName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
