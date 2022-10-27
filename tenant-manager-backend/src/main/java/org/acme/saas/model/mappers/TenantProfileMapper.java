package org.acme.saas.model.mappers;

import org.acme.saas.model.Tenant;
import org.acme.saas.model.draft.TenantProfileDraft;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TenantProfileMapper {

    TenantProfileMapper INSTANCE = Mappers.getMapper(TenantProfileMapper.class);

    @Mapping(target = "password", ignore = true)
    TenantProfileDraft tenantToTenantProfileDraft(Tenant tenant);
}
