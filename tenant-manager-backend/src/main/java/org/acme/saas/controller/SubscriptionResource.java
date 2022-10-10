package org.acme.saas.controller;

import io.smallrye.mutiny.Uni;
import org.acme.saas.model.data.PriceData;
import org.acme.saas.model.draft.SubscriptionDraft;
import org.acme.saas.model.mappers.SubscriptionMapper;
import org.acme.saas.service.SubscriptionService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/subscribe")
public class SubscriptionResource {

    @Inject
    SubscriptionService subscriptionService;

    @POST
    @Path("/calculate-price")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String calculatePrice(PriceData priceData) {
        double priceWithoutFormatting = subscriptionService.
                calculatePrice(priceData.getTier(), priceData.getAvgConcurrentShoppers());
        return String.format("%.2f", priceWithoutFormatting);
    }

    @GET
    @Path("/{tenantKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<SubscriptionDraft> getSubscriptionByTenantKey(String tenantKey) {
        return subscriptionService.findByTenantKey(tenantKey)
                .onItem().ifNotNull()
                .transform(SubscriptionMapper.INSTANCE::subscriptionToSubscriptionDraft)
                .onItem().ifNull().failWith(NotFoundException::new);
    }
}
