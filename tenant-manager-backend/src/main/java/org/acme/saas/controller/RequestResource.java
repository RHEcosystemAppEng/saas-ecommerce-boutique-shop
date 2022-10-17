package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.common.Constants;
import org.acme.saas.model.data.RequestData;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.model.data.TokenData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.service.RequestService;
import org.acme.saas.service.TenantService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/request")
public class RequestResource {

    @Inject
    RequestService requestService;

    @Inject
    TenantService tenantService;

    @GET
    @Path("/pending")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<RequestChangeData>> getPendingRequests() {
        return requestService.getRequestChangeData();
    }

    @POST
    @Path("/resource")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokenData> createNewRequest(RequestData requestData) {
        RequestDraft.RequestDraftBuilder requestDraftBuilder = RequestDraft.builder();
        requestDraftBuilder.tenantKey(requestData.getTenantKey());
        requestDraftBuilder.tier(requestData.getTier());
        requestDraftBuilder.avgConcurrentShoppers(requestData.getAvgConcurrentShoppers());
        requestDraftBuilder.peakConcurrentShoppers(requestData.getPeakConcurrentShoppers());
        requestDraftBuilder.status(Constants.REQUEST_STATUS_PENDING);

        return requestService.createNewRequest(requestDraftBuilder.build())
                .onItem().ifNotNull()
                .transformToUni(request -> tenantService.findByTenantKey(request.getTenantKey()))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(tenant -> {
                    TokenData.TokenDataBuilder tokenDataBuilder = TokenData.builder();
                    tokenDataBuilder.key(tenant.getTenantKey());
                    tokenDataBuilder.Id(tenant.id);
                    tokenDataBuilder.loggedInUserName(tenant.getTenantName());
                    return tokenDataBuilder.build();
                });
    }
}
