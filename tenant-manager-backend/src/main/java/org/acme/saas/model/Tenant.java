package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
public class Tenant extends PanacheEntity {

    private String tenantKey;
    private String tenantName;
    private String orgName;
    private String address;
    private String phone;
    private String contactName;
    private String email;
    private String password;
    private String status;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Subscription> subscriptions = new ArrayList<>();

    public static Uni<Tenant> findByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).firstResult();
    }

    public static Uni<Tenant> findTenantByEmailAndPassword(String email, String password) {
        return find("email= ?1 and password = ?2", email, password).firstResult();
    }
}
