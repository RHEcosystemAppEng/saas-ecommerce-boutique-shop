package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.draft.TokenData;
import org.acme.saas.model.draft.TokenData.TokenDataBuilder;
import org.acme.saas.service.TenantService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/tenant")
public class TenantResource {
    @Inject
    TenantService tenantService;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> healthEndpoint() {
        return Uni.createFrom().item("ok");
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Tenant> getTenantById(Long id) {
        return tenantService.getTenantById(id);
    }


    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokenData> login(String email, String password) {
        TokenDataBuilder tokenDataBuilder = TokenData.builder();
        tenantService.findTenant(email, password).subscribe().with(
                tenant -> {
                    tokenDataBuilder.Id(tenant.getId());
                    tokenDataBuilder.key(tenant.getTenantKey());
                    tokenDataBuilder.loggedInUserName(tenant.getTenantUserName());
                }
        );
        return Uni.createFrom().item(tokenDataBuilder.build());
    }

    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokenData> signUp(TenantDraft tenantDraft) {

        TokenDataBuilder tokenDataBuilder = TokenData.builder();
        Uni<Tenant> savedTenantUni = tenantService.save(tenantDraft);

        savedTenantUni.subscribe().with(
                tenant -> {
                    tokenDataBuilder.Id(tenant.getId());
                    tokenDataBuilder.key(tenant.getTenantKey());
                    tokenDataBuilder.loggedInUserName(tenant.getTenantUserName());
                }
        );

        return Uni.createFrom().item(tokenDataBuilder.build());
    }

    @GET
    @Path("/email/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Boolean> isEmailAlreadyInUse(String email) {
        return tenantService.isEmailAlreadyInUse(email);
    }
}
