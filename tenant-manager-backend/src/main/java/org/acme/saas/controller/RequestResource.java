package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.common.Constants;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.model.data.RequestData;
import org.acme.saas.model.data.TokenData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.service.RequestService;
import org.acme.saas.service.TenantService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/request")
public class RequestResource {

    @Inject
    RequestService requestService;

    @Inject
    TenantService tenantService;

    @Operation(summary = "Returns the list of all the pending request")
    @APIResponse(responseCode = "200", description = "Pending requests", content = @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = RequestChangeData.class, type = SchemaType.ARRAY)))
    @GET
    @Path("/pending")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<RequestChangeData>> getPendingRequests() {
        return requestService.getRequestChangeData();
    }

    @Operation(summary = "Submits a new request to update the capacity of a givent Tenant, identified by the " +
            "tenantKey ")
    @APIResponse(responseCode = "200", description = "Request submitted in PENDING state", content =
    @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = TokenData.class)))
    @APIResponse(responseCode = "404", description = "No Tenant was found for the given tenantKey")
    @POST
    @Path("/resource")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokenData> createNewRequest(@RequestBody(
            required = true,
            content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = RequestData.class))) RequestData requestData) {
        RequestDraft.RequestDraftBuilder requestDraftBuilder = RequestDraft.builder();
        requestDraftBuilder.tenantKey(requestData.getTenantKey());
        requestDraftBuilder.tier(requestData.getTier());
        requestDraftBuilder.avgConcurrentShoppers(requestData.getAvgConcurrentShoppers());
        requestDraftBuilder.peakConcurrentShoppers(requestData.getPeakConcurrentShoppers());
        requestDraftBuilder.status(Constants.REQUEST_STATUS_PENDING);

        return requestService.createNewRequest(requestDraftBuilder.build())
                .onItem().ifNotNull()
                .transformToUni(request -> tenantService.findByTenantKey(request.tenantKey))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(tenant -> {
                    TokenData.TokenDataBuilder tokenDataBuilder = TokenData.builder();
                    tokenDataBuilder.key(tenant.tenantKey);
                    tokenDataBuilder.Id(tenant.id);
                    tokenDataBuilder.loggedInUserName(tenant.tenantName);
                    return tokenDataBuilder.build();
                });
    }

    @Operation(summary = "Approves a pending request, identified by its requestId, and updates the provisioned " +
            "resources to match the requested updates")
    @APIResponse(responseCode = "200", description = "Request approved and provisioned resources updated", content =
    @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = RequestDraft.class)))
    @APIResponse(responseCode = "404", description = "No request was found for the given requestId")
    @APIResponse(responseCode = "400", description = "The request is not in the expected PENDING state")
    @PUT
    @Path("/{id}/approve")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RequestDraft> approveByRequestId(@Parameter(description = "requestId of the pending request",
            required = true) @PathParam("id") long id) {
        return requestService.approveByRequestId(id);
    }

    @Operation(summary = "Rejects a pending request, identified by its requestId")
    @APIResponse(responseCode = "200", description = "Request rejected", content =
    @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = RequestDraft.class)))
    @APIResponse(responseCode = "404", description = "No request was found for the given requestId")
    @APIResponse(responseCode = "400", description = "The request is not in the expected PENDING state")
    @PUT
    @Path("/{id}/reject")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<RequestDraft> rejectByRequestId(@Parameter(description = "requestId of the pending request", required
            = true) @PathParam("id") long id) {
        return requestService.rejectByRequestId(id);
    }
}
