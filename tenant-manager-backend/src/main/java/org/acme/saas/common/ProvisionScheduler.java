package org.acme.saas.common;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Request;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.ProvisionRequest;
import org.acme.saas.model.data.ProvisionResponse;
import org.acme.saas.service.ProvisionService;
import org.acme.saas.service.TenantService;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class ProvisionScheduler {

    private final Logger log = Logger.getLogger(ProvisionScheduler.class);
    private final AtomicBoolean running = new AtomicBoolean(false);
    @Inject
    TenantService tenantService;

    @Inject
    ProvisionService provisionService;

    @Scheduled(every = "200s")
    void increment() {
        if (running.get())
            return;

        running.set(true);

        log.info("Provisioning scheduler started...");
        tenantService.findAllTenantsForResourceCalculation().onItem()
                .ifNotNull().transformToUni(this::sendDmnRequests)
                .onItem().invoke(provisionResponses -> {
                    running.set(false);
                    log.info("Provisioning scheduler finished.");
                })
                .await().indefinitely();
    }

    private Uni<List<ProvisionResponse>> sendDmnRequests(List<Tenant> tenantList) {
        List<Tenant> freeTierTenants = new ArrayList<>();
        List<Tenant> silverTierTenants = new ArrayList<>();
        List<Tenant> goldTierTenants = new ArrayList<>();
        List<Tenant> platinumTierTenants = new ArrayList<>();

        tenantList.forEach(e -> {
            switch (e.subscriptions.get(0).tier.toLowerCase()) {
                case "free" -> freeTierTenants.add(e);
                case "silver" -> silverTierTenants.add(e);
                case "gold" -> goldTierTenants.add(e);
                case "platinum" -> platinumTierTenants.add(e);
            }
        });

        return Uni.combine().all().unis(
                        sendDmnRequest("free", freeTierTenants),
                        sendDmnRequest("silver", silverTierTenants),
                        sendDmnRequest("gold", goldTierTenants),
                        sendDmnRequest("platinum", platinumTierTenants))
                .combinedWith((provisionResponse, provisionResponse2, provisionResponse3, provisionResponse4) -> {
                    List<ProvisionResponse> requests = new ArrayList<>();
                    requests.add(provisionResponse);
                    requests.add(provisionResponse2);
                    requests.add(provisionResponse3);
                    requests.add(provisionResponse4);
                    return requests;
                });
    }

    private Uni<ProvisionResponse> sendDmnRequest(String tier, List<Tenant> tierTenants) {
        ProvisionRequest provisionRequest = new ProvisionRequest();
        int shopperCount = 0;
        final boolean[] shouldCreateRequest = {false};
        ProvisionRequest.TierRequest.TierRequestBuilder tierRequestBuilder = ProvisionRequest.TierRequest.builder();
        tierRequestBuilder.tier(tier);
        ProvisionRequest.TierRequest tierRequest = tierRequestBuilder.build();
        List<ProvisionRequest.TenantRequest> tmpList = new ArrayList<>();

        for (Tenant tenant : tierTenants) {
            if (tenant.status.equals(Constants.TENANT_STATUS_RUNNING)) {
                shopperCount += tenant.subscriptions.get(0).request.avgConcurrentShoppers;
            }
            if (!tenant.desiredState) {
                ProvisionRequest.TenantRequest.TenantRequestBuilder tenantRequestBuilder = ProvisionRequest.TenantRequest.builder();
                shouldCreateRequest[0] = true;
                Request request = tenant.subscriptions.get(0).request;
                tenantRequestBuilder.tenant(request.tenantKey);
                tenantRequestBuilder.averageConcurrentShoppers(String.valueOf(request.avgConcurrentShoppers));

                tmpList.add(tenantRequestBuilder.build());
            }
        }
        tierRequest.setTenantRequests(tmpList);
        tierRequest.setNewTotalAverageConcurrentShoppers(String.valueOf(shopperCount));
        provisionRequest.setTierRequests(Collections.singletonList(tierRequest));

        if (shouldCreateRequest[0]) {
            return provisionService.provisionTier(provisionRequest)
                    .onItem().transform(provisionResponse -> {
                        log.infof("Printing response: %s", provisionResponse);
                        return provisionResponse;
                    });
        }
        return Uni.createFrom().item(() -> null);
    }
}
