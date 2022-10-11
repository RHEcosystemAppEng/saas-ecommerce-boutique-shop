package org.acme.saas.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.data.SummaryData;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class SubscriptionRepository implements PanacheRepository<Subscription> {

    @Inject
    EntityManager entityManager;

    public Uni<Subscription> findByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).firstResult();
    }

    public Uni<List<SummaryData>> getSubscriptionSummary() {

        return find("SELECT s.tier, COUNT(*), SUM(r.avgConcurrentShoppers) FROM Subscription s, Request r where s.tenantKey = r.tenantKey AND r.status='APPROVED' GROUP BY s.tier")
                .project(SummaryData.class).list();
    }
}
