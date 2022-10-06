package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.draft.TokenData;
import org.acme.saas.service.TenantService;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/tenant")
public class TenantResource {
    @Inject
    TenantService tenantService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Tenant> test(TenantDraft tenantDraft) {
        return tenantService.save(tenantDraft);
    }

    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokenData> signUp(TenantDraft tenantDraft) {

        Uni<Tenant> savedTenantUni = tenantService.save(tenantDraft);
        TokenData data = new TokenData();
        Uni<TokenData> tokenDataUni = Uni.createFrom().item(data);

        savedTenantUni.subscribe().with(
                tenant -> {
                    data.setId(tenant.getId());
                    data.setKey(tenant.getTenantKey());
                    data.setLoggedInUserName(tenant.getTenantUserName());
                }
        );

        return tokenDataUni;
    }
}
