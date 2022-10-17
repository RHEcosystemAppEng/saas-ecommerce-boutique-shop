package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.acme.saas.common.Constants;
import org.acme.saas.model.data.SubscriptionSummaryData;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Subscription extends PanacheEntity {

    private String tenantKey;
    private String serviceName;
    private String tier;
    private int minInstanceCount;
    private int maxInstanceCount;
    private String url;

    @OneToOne
    private Request request;
    private String status;

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
