package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Request;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.mappers.RequestMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class RequestService {

    @ReactiveTransactional
    public Uni<Request> createNewRequest(RequestDraft requestDraft) {
        Request request = RequestMapper.INSTANCE.requestDraftToRequest(requestDraft);
        return request.persist();
    }

    @Inject
    SubscriptionService subscriptionService;

    @ReactiveTransactional
    public Uni<List<RequestChangeData>> getRequestChangeData() {
        return Uni.combine().all().unis(
                Request.findAllPendingRequests(),
                Tenant.findAllActiveTenants()
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
