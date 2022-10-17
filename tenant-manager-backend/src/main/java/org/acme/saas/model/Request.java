package org.acme.saas.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.Setter;
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

@Getter
@Setter
@Entity
@ToString
public class Request extends PanacheEntity {

    private long id;
    private String tenantKey;
    private String hostName;
    private String serviceName;
    private String tier;
    private int avgConcurrentShoppers;
    private int peakConcurrentShoppers;
    private String fromTime;
    private String toTime;
    private String status;

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
                    .collect(Collectors.groupingBy(Tenant::getTenantKey));

            for (Request request : pendingRequests) {
                int[] instanceCount = subscriptionService.calculateInstanceCount(request.getAvgConcurrentShoppers());

                RequestChangeData changeData = new RequestChangeData();
                changeData.setTenantKey(request.getTenantKey());
                changeData.setNewTier(request.getTier());
                changeData.setServiceName(request.getServiceName());
                changeData.setNewMinInstances(instanceCount[0]);
                changeData.setNewMaxInstances(instanceCount[1]);

                Tenant tenant = tenantMap.get(request.getTenantKey()).get(0);
                changeData.setTenantName(tenant.getTenantName());

                tenant.getSubscriptions().stream().limit(1)
                        .forEach(subscription -> {
                            changeData.setCurrentTier(subscription.getTier());
                            changeData.setOldMinInstances(subscription.getMinInstanceCount());
                            changeData.setOldMaxInstances(subscription.getMaxInstanceCount());
                        });

                data.add(changeData);
            }
            return data;
        });
    }
}
