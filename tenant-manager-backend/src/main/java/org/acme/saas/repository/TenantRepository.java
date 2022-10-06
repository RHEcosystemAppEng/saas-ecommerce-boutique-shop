package org.acme.saas.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Tenant;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TenantRepository implements PanacheRepository<Tenant> {

    public Uni<Tenant> findTenantByEmailAndPassword(String email, String password) {
        return find("email= ?1 and password = ?2", email, password).firstResult();
    }
}
