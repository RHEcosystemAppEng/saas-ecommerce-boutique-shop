package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.data.PriceData;
import org.acme.saas.model.data.SubscriptionSummaryData;
import org.acme.saas.model.draft.SubscriptionDraft;
import org.acme.saas.model.mappers.SubscriptionMapper;
import org.acme.saas.service.SubscriptionService;
import org.eclipse.microprofile.openapi.annotations.Operation;
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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/subscribe")
public class SubscriptionResource {

    @Inject
    SubscriptionService subscriptionService;

    @Operation(summary = "Calculates the monthly subscription fee for a given subscription")
    @APIResponse(responseCode = "200", description = "Calculated price formatted to the 2nd decimal digit", content =
    @Content(mediaType = APPLICATION_JSON))
    @POST
    @Path("/calculate-price")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String calculatePrice(@RequestBody(
            required = true,
            content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = PriceData.class))) PriceData priceData) {
        double priceWithoutFormatting = subscriptionService.
                calculatePrice(priceData.getTier(), priceData.getAvgConcurrentShoppers());
        return String.format("%.2f", priceWithoutFormatting);
    }

    @Operation(summary = "Returns the current subscriptions for a given Tenant, identified by the tenantKey")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema =
    @Schema(implementation = SubscriptionDraft.class)))
    @APIResponse(responseCode = "404", description = "No Tenant found by the given tenantKey")
    @GET
    @Path("/{tenantKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<SubscriptionDraft> getSubscriptionByTenantKey(@Parameter(description = "tenantKey of the Tenant",
            required = true) String tenantKey) {
        return subscriptionService.findFirstByTenantKey(tenantKey)
                .onItem().ifNotNull()
                .transform(SubscriptionMapper.INSTANCE::subscriptionToSubscriptionDraft)
                .onItem().ifNull().failWith(NotFoundException::new);
    }

    @Operation(summary = "Returns a summary of the current subcriptions")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema =
    @Schema(implementation = SubscriptionSummaryData.class)))
    @GET
    @Path("/summary")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<SubscriptionSummaryData>> getSubscriptionSummary() {
        return subscriptionService.getSubscriptionSummary();
    }

    @Operation(summary = "Returns the current subscriptions for a given Tenant, identified by the url")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema =
    @Schema(implementation = SubscriptionDraft.class)))
    @APIResponse(responseCode = "404", description = "No Tenant found by the given tenantKey")
    @GET

    @Path("/host/{hostname}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<SubscriptionDraft> getSubscriptionByHostname(@Parameter(description = "Url of the subscription",
            required = true) String hostname) {
        return subscriptionService.findFirstByHostname(hostname)
                .onItem().ifNotNull()
                .transform(SubscriptionMapper.INSTANCE::subscriptionToSubscriptionDraft)
                .onItem().ifNull().failWith(NotFoundException::new);
    }

}
