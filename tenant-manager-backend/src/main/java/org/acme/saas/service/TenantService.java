package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.common.Constants;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.data.TenantData;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.mappers.TenantMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class TenantService {

    @Inject
    SubscriptionService subscriptionService;

    @ReactiveTransactional
    public Uni<Tenant> login(LoginData loginData) {
        return Tenant.findTenantByEmailAndPassword(
                loginData.getEmail(),
                loginData.getPassword());
    }

    public String generateTenantKey(String seed) {
        String alphanumeric = seed.replaceAll("\\s", "").toLowerCase();
        if (alphanumeric.length() > 5)
            alphanumeric = alphanumeric.substring(0, 6);
        String beforeCut = alphanumeric + new StringBuilder().append(System.currentTimeMillis()).reverse();

        return beforeCut.substring(0, 11);
    }

    @ReactiveTransactional
    public Uni<Tenant> findByTenantKey(String tenantKey) {
        return Tenant.findByTenantKey(tenantKey);
    }

    @ReactiveTransactional
    public Uni<List<Tenant>> findAllActiveTenants() {
        return Tenant.findAllTenantsByStatus(Constants.TENANT_STATUS_RUNNING);
    }

    @ReactiveTransactional
    public Uni<List<TenantData>> findAllTenants() {
        return Tenant.findAllTenants().onItem()
                .transform(list -> {
                    List<TenantData> tenantDataList = new ArrayList<>();
                    for (Tenant tenant : list) {
                        TenantData.TenantDataBuilder builder = TenantData.builder();
                        builder.tenantKey(tenant.tenantKey);
                        builder.tenantName(tenant.tenantName);
                        builder.status(tenant.status);
                        builder.tier(tenant.subscriptions.get(0).tier);
                        builder.avgConcurrentShoppers(String.valueOf(tenant.subscriptions.get(0).request.avgConcurrentShoppers));
                        builder.peakConcurrentShoppers(String.valueOf(tenant.subscriptions.get(0).request.peakConcurrentShoppers));
                        tenantDataList.add(builder.build());
                    }
                    return tenantDataList;
                });
    }

    @ReactiveTransactional
    public Uni<Boolean> isEmailAlreadyInUse(String email) {
        return Tenant.find("email", email)
                .firstResult()
                .map(Objects::nonNull);
    }

    @ReactiveTransactional
    public Uni<Tenant> createNewTenant(TenantDraft tenantDraft, Subscription subscription) {
        Tenant tenant = TenantMapper.INSTANCE.tenantDraftToTenant(tenantDraft);
        tenant.status = Constants.TENANT_STATUS_RUNNING;
        tenant.subscriptions = List.of(subscription);

        return tenant.persist();
    }

    @ReactiveTransactional
    public Uni<TenantData> updateTenantStatus(String tenantKey, String newStatus) {
        return Tenant.findByTenantKey(tenantKey).onItem()
                .transform(tenant -> {
                    tenant.desiredState = false;
                    tenant.status = newStatus;
                    return tenant;
                })
                .onItem().transformToUni(tenant -> Tenant.persist(tenant))
                .onItem().transform(tenant -> {
                    TenantData.TenantDataBuilder builder = TenantData.builder();
                    builder.tenantKey(tenant.tenantKey);
                    builder.tenantName(tenant.tenantName);
                    builder.tier(tenant.subscriptions.get(0).tier);
                    builder.status(tenant.status);
                    builder.peakConcurrentShoppers(String.valueOf(tenant.subscriptions.get(0).request.peakConcurrentShoppers));
                    builder.avgConcurrentShoppers(String.valueOf(tenant.subscriptions.get(0).request.avgConcurrentShoppers));
                    return builder.build();
                });
    }

    @ReactiveTransactional
    public Uni<List<Tenant>> findAllTenantsForResourceCalculation() {
        Uni<List<Tenant>> modifiedTenants = Tenant.findAllTenantsByDesiredStatus(false);
        Uni<List<Tenant>> runningTenants = Tenant.findAllTenantsByStatus(Constants.TENANT_STATUS_RUNNING);
        return Uni.combine()
                .all()
                .unis(modifiedTenants, runningTenants)
                .combinedWith((modified, running) -> {
                    List<Tenant> noChangeRequiredTenants = running.stream().filter(tenant -> tenant.desiredState).toList();
                    modified.addAll(noChangeRequiredTenants);
                    return modified;
                });
    }

    @ReactiveTransactional
    public Uni<Tenant> updateTenantSubscriptions(Tenant tenantToUpdate, Collection<Subscription> subscriptions) {
        return Tenant.update("subscriptions = ?1 where id = ?2", subscriptions, tenantToUpdate.id)
                .onItem().transformToUni(integer -> Tenant.findById(tenantToUpdate.id));
    }
}
