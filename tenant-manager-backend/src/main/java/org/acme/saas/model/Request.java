package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import lombok.ToString;
import org.acme.saas.common.Constants;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.service.SubscriptionService;
import org.acme.saas.service.TenantService;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@ToString
public class Request extends PanacheEntity {

    public String tenantKey;
    public String hostName;
    public String serviceName;
    public String tier;
    public int avgConcurrentShoppers;
    public int peakConcurrentShoppers;
    public String fromTime;
    public String toTime;
    public String status;

    @Inject
    static TenantService tenantService;

    @Inject
    @Transient
    static SubscriptionService subscriptionService;

    public static Uni<List<Request>> findAllByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).list();
    }

    public static Uni<List<Request>> findAllPendingRequests() {
        return find("status=?1", Constants.REQUEST_STATUS_PENDING).list();
    }

    public static Uni<List<RequestChangeData>> getRequestChangeData() {
        return Uni.combine().all().unis(
                findAllPendingRequests(),
                tenantService.findAllActiveTenants()
        ).combinedWith((pendingRequests, tenants) -> {
            List<RequestChangeData> data = new ArrayList<>();
            Map<String, List<Tenant>> tenantMap = tenants.stream()
                    .collect(Collectors.groupingBy(tenant -> tenant.tenantKey));

            for (Request request : pendingRequests) {
                int[] instanceCount = subscriptionService.calculateInstanceCount(request.avgConcurrentShoppers);

                RequestChangeData changeData = new RequestChangeData();
                changeData.setTenantKey(request.tenantKey);
                changeData.setNewTier(request.tier);
                changeData.setServiceName(request.serviceName);
                changeData.setNewMinInstances(instanceCount[0]);
                changeData.setNewMaxInstances(instanceCount[1]);

                Tenant tenant = tenantMap.get(request.tenantKey).get(0);
                changeData.setTenantName(tenant.tenantName);

                tenant.subscriptions.stream().limit(1)
                        .forEach(subscription -> {
                            changeData.setCurrentTier(subscription.tier);
                            changeData.setOldMinInstances(subscription.minInstanceCount);
                            changeData.setOldMaxInstances(subscription.maxInstanceCount);
                        });

                data.add(changeData);
            }
            return data;
        });
    }
}
