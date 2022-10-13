package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.common.Constants;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.mappers.TenantMapper;
import org.acme.saas.repository.TenantRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@ApplicationScoped
public class TenantService {

    @Inject
    TenantRepository tenantRepository;

    @Inject
    SubscriptionService subscriptionService;

    @ReactiveTransactional
    public Uni<Tenant> login(LoginData loginData) {
        return tenantRepository.findTenantByEmailAndPassword(
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
        return tenantRepository.findByTenantKey(tenantKey);
    }

    @ReactiveTransactional
    public Uni<Boolean> isEmailAlreadyInUse(String email) {
        return tenantRepository.find("email", email)
                .firstResult()
                .map(Objects::nonNull);
    }

    @ReactiveTransactional
    public Uni<Tenant> createNewTenant(TenantDraft tenantDraft, Subscription subscription) {
        Tenant tenant = TenantMapper.INSTANCE.tenantDraftToTenant(tenantDraft);
        tenant.setStatus(Constants.TENANT_STATUS_ACTIVE);
        tenant.setSubscriptions(List.of(subscription));

        return tenantRepository.persist(tenant);
    }

    @ReactiveTransactional
    public Uni<Tenant> updateTenantSubscriptions(Tenant tenantToUpdate, Collection<Subscription> subscriptions) {
        return tenantRepository.update("subscriptions = ?1 where id = ?2", subscriptions, tenantToUpdate.getId())
                .onItem().transformToUni(integer -> tenantRepository.findById(tenantToUpdate.getId()));
    }
}
