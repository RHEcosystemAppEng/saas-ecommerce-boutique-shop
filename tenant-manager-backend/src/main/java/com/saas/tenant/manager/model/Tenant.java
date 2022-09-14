package com.saas.tenant.manager.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Entity
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String tenantUserName;
    private String orgName;
    private String address;
    private String phone;
    private String contactPerson;
    private String email;
    private String password;
    private boolean status;

    @OneToMany(mappedBy = "tenant")
    private Set<Subscription> subscriptionSet;

    public Collection<Subscription> getSubscriptionSet() {
        return subscriptionSet;
    }

    public void setSubscriptionSet(Collection<Subscription> subscriptionSet) {
        this.subscriptionSet = (Set<Subscription>) subscriptionSet;
    }
}
