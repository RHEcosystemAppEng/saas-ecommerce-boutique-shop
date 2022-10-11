package org.acme.saas.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Request;
import org.acme.saas.model.Tenant;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RequestRepository implements PanacheRepository<Request> {

    public Uni<Request> findByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).firstResult();
    }
}
