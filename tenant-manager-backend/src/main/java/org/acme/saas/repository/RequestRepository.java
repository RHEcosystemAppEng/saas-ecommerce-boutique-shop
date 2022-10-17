package org.acme.saas.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import org.acme.saas.common.Constants;
import org.acme.saas.model.Request;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.service.SubscriptionService;
import org.acme.saas.service.TenantService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class RequestRepository implements PanacheRepository<Request> {

    @Inject
    TenantService tenantService;

    @Inject
    SubscriptionService subscriptionService;

    public Uni<List<Request>> findAllByTenantKey(String tenantKey) {
        return find("tenantKey= ?1", tenantKey).list();
    }

    public Uni<List<Request>> findAllPendingRequests() {
        return find("status=?1", Constants.REQUEST_STATUS_PENDING).list();
    }

    public Uni<List<RequestChangeData>> getRequestChangeData() {
        return Uni.combine().all().unis(
                findAllPendingRequests(),
                tenantService.findAllActiveTenants()
        ).combinedWith((pendingRequests, tenants) -> {
            List<RequestChangeData> data = new ArrayList<>();
            Map<String, List<Tenant>> tenantMap = tenants.stream()
                    .collect(Collectors.groupingBy(Tenant::getTenantKey));

            for (Request request : pendingRequests) {
                int[] instanceCount = subscriptionService.calculateInstanceCount(request.getAvgConcurrentShoppers(), request.getPeakConcurrentShoppers());

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
