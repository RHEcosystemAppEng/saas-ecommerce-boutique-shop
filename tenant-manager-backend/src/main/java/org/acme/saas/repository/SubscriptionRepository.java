package org.acme.saas.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import org.acme.saas.common.Constants;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.data.SubscriptionSummaryData;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SubscriptionRepository implements PanacheRepository<Subscription> {

    public Uni<List<Subscription>> findAllByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).list();
    }

    public Uni<Subscription> findFirstByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).firstResult();
    }

    public Uni<List<SubscriptionSummaryData>> getSubscriptionSummary() {
        return find(Constants.SQL_QUERY_SUBSCRIPTION_SUMMARY)
                .project(SubscriptionSummaryData.class).list();
    }
}
