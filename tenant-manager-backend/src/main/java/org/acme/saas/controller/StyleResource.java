package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Style;
import org.acme.saas.model.draft.StyleDraft;
import org.acme.saas.model.mappers.StyleMapper;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/style")
public class StyleResource {

    @Operation(summary = "Returns the styles for a given Tenant, identified by the tenantKey")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema =
    @Schema(implementation = StyleDraft.class)))
    @APIResponse(responseCode = "404", description = "No Style found by the given tenantKey")
    @GET
    @Path("/{tenantKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<StyleDraft> getStyleByTenantKey(@Parameter(description = "tenantKey of the Style",
            required = true) String tenantKey) {
        return Style.findByTenantKey(tenantKey)
                .onItem().ifNotNull().transform(StyleMapper.INSTANCE::styleToStyleDraft)
                .onItem().ifNull().failWith(NotFoundException::new);
    }
}
