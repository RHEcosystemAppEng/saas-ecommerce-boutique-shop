package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.mappers.TenantMapper;
import org.acme.saas.repository.TenantRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TenantService {

    @Inject
    private TenantRepository tenantRepository;

    @ReactiveTransactional
    public Uni<Tenant> save(TenantDraft tenantDraft) {
        Tenant tenant = TenantMapper.INSTANCE.tenantDraftToTenant(tenantDraft);
        tenant.setStatus(true);
        return tenantRepository.persist(tenant);
    }
}
