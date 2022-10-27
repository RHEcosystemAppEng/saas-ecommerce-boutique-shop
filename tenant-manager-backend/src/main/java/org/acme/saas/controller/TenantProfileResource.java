package org.acme.saas.controller;

import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.draft.TenantProfileDraft;
import org.acme.saas.model.mappers.TenantProfileMapper;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("tenant-profile")
public class TenantProfileResource {
    @Operation(summary = "Returns all the Tenant profiles")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema =
    @Schema(implementation = TenantProfileDraft.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "404", description = "No Tenant found by the given tenantKey")
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    public Multi<TenantProfileDraft> allTenantProfiles() {
        return Tenant.findAll(Sort.by("tenantName")).<Tenant>stream()
                .onItem()
                .transform(TenantProfileMapper.INSTANCE::tenantToTenantProfileDraft);
    }

    @Operation(summary = "Return a single Tenant profile given its tenantKey")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema =
    @Schema(implementation = TenantProfileDraft.class)))
    @APIResponse(responseCode = "404", description = "No Tenant found by the given tenantKey")
    @GET
    @Path("/{tenantKey}")
    @Produces(APPLICATION_JSON)
    public Uni<TenantProfileDraft> tenantProfileByTenantKey(@Parameter(description = "tenantKey of the Tenant",
            required =
                    true) @PathParam("tenantKey") String tenantKey) {
        return Tenant.findByTenantKey(tenantKey)
                .onItem().transform(TenantProfileMapper.INSTANCE::tenantToTenantProfileDraft)
                .onItem().ifNull().failWith(NotFoundException::new);
    }
}
