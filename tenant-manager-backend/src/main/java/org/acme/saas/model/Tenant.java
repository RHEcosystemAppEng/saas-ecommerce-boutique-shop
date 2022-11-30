package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
public class Tenant extends PanacheEntity {

    public String tenantKey;
    public String tenantName;
    public String orgName;
    public String address;
    public String phone;
    public String contactName;
    public String email;
    public String password;
    public boolean desiredState;
    public String status;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "TENANT_ID")
    public List<Subscription> subscriptions = new ArrayList<>();

    public static Uni<Tenant> findByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).firstResult();
    }

    public static Uni<Tenant> findTenantByEmailAndPassword(String email, String password) {
        return find("email= ?1 and password = ?2", email, password).firstResult();
    }

    public static Uni<List<Tenant>> findAllTenantsByStatus(String status) {
        return find("status=?1", status).list();
    }

    public static Uni<List<Tenant>> findAllTenantsByDesiredStatus(Boolean desired) {
        return find("desiredState=?1", desired).list();
    }

    public static Uni<List<Tenant>> findAllTenants() {
        return findAll().list();
    }

    public static Uni<Tenant> persist(Tenant tenant) {
        return tenant.persist();
    }
}
