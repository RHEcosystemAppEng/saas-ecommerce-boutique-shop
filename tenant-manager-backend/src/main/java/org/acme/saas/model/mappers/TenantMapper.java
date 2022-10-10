package org.acme.saas.model.mappers;

import org.acme.saas.model.Tenant;
import org.acme.saas.model.draft.TenantDraft;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TenantMapper {

    TenantMapper INSTANCE = Mappers.getMapper(TenantMapper.class);
    Tenant tenantDraftToTenant(TenantDraft draft);

    @Mapping(target = "password", ignore = true)
    TenantDraft tenantToTenantDraft(Tenant tenant);
}
