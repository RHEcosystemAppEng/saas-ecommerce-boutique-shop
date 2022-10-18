package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import lombok.ToString;
import org.acme.saas.common.Constants;
import org.acme.saas.model.data.SubscriptionSummaryData;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
@ToString
public class Subscription extends PanacheEntity {

    public String tenantKey;
    public String serviceName;
    public String tier;
    public int minInstanceCount;
    public int maxInstanceCount;
    public String url;

    @OneToOne
    public Request request;
    public String status;

    public static Uni<List<Subscription>> findAllByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).list();
    }

    public static Uni<Subscription> findFirstByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).firstResult();
    }

    public static Uni<List<SubscriptionSummaryData>> getSubscriptionSummary() {
        return find(Constants.SQL_QUERY_SUBSCRIPTION_SUMMARY)
                .project(SubscriptionSummaryData.class).list();
    }
}
