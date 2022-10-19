package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import lombok.ToString;
import org.acme.saas.common.Constants;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    public String status;

    @OneToMany(fetch = FetchType.EAGER)
    public List<Subscription> subscriptions = new ArrayList<>();

    public static Uni<Tenant> findByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).firstResult();
    }

    public static Uni<Tenant> findTenantByEmailAndPassword(String email, String password) {
        return find("email= ?1 and password = ?2", email, password).firstResult();
    }

    public static Uni<List<Tenant>> findAllActiveTenants() {
        return find("status=?1", Constants.TENANT_STATUS_ACTIVE).list();
    }
}
