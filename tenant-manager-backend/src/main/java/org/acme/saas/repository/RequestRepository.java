package org.acme.saas.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Request;
import org.acme.saas.model.data.RequestChangeData;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class RequestRepository implements PanacheRepository<Request> {

    public Uni<Request> findByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).firstResult();
    }

    public Uni<List<RequestChangeData>> getRequestChangeData() {
        return null;
    }
}
