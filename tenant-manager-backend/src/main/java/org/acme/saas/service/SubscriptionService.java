package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Request;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.data.CostComputationRequest;
import org.acme.saas.model.data.CostComputationResponse;
import org.acme.saas.model.data.HpaComputationRequest;
import org.acme.saas.model.data.SubscriptionSummaryData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.draft.SubscriptionDraft;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.mappers.SubscriptionMapper;
import org.acme.saas.restclient.RulesClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class SubscriptionService {
    private Logger log = Logger.getLogger(SubscriptionService.class);

    @RestClient
    RulesClient rulesClient;

    @Inject
    RequestService requestService;
    @Inject
    ProvisionService provisionService;

    @ReactiveTransactional
    public Uni<Subscription> findFirstByTenantKey(String tenantKey) {
        return Subscription.findFirstByTenantKey(tenantKey);
    }

    @ReactiveTransactional
    public Uni<Subscription> findFirstByHostname(String hostname) {
        return Subscription.findFirstByHostname(hostname);
    }

    @ReactiveTransactional
    public Uni<List<Subscription>> findAllByTenantKey(String tenantKey) {
        return Subscription.findAllByTenantKey(tenantKey);
    }

    public Uni<Double> calculatePrice(String tier, int avgConcurrentShoppers) {

        CostComputationRequest request = new CostComputationRequest();
        request.setTier(tier);
        request.setAverageConcurrentShoppers(String.valueOf(avgConcurrentShoppers));
        return rulesClient.calculatePrice(request)
                .onItem().transform(CostComputationResponse::getCalculatedPrice);
    }

    public int[] calculateInstanceCount(int avgConcurrentShoppers, int peakConcurrentShoppers) {
        int min = Math.max(1, avgConcurrentShoppers / 50);
        int max = Math.max(1, peakConcurrentShoppers / 50);

        return new int[]{min, max};
    }

    public Uni<int[]> calculateInstanceCount(String tier, int avgConcurrentShoppers) {
        HpaComputationRequest request = new HpaComputationRequest();
        request.setTier(tier.toLowerCase());
        request.setBucket("shopper experience");
        request.setAverageConcurrentShoppers(avgConcurrentShoppers);
        return rulesClient.calculateHpa(request)
                .onItem().transform(
                        hpaComputationResponse -> new int[]{
                                hpaComputationResponse.getHpaReplicas().getMinReplicas(),
                                hpaComputationResponse.getHpaReplicas().getMaxReplicas()}
                );
    }

    @ReactiveTransactional
    public Uni<Subscription> createNewSubscription(TenantDraft tenantDraft, SubscriptionDraft subscriptionDraft,
                                                   RequestDraft requestDraft) {

        Uni<Request> requestUni = requestService.createNewRequest(requestDraft);
        Uni<String> serviceUrlUni = provisionService.onNewSubscription(tenantDraft, requestDraft);

        return Uni.combine().all().unis(requestUni, serviceUrlUni).combinedWith(
                        (request, serviceUrl) -> {
                            Subscription subscription = SubscriptionMapper.INSTANCE
                                    .subscriptionDraftToSubscription(subscriptionDraft);

                            subscription.request = request;
                            subscription.url = serviceUrl;
                            return subscription.<Subscription>persist();
                        }
                )
                .onItem().ifNull().failWith(InternalServerErrorException::new)
                .flatMap(Function.identity());
    }


    public Uni<List<SubscriptionSummaryData>> getSubscriptionSummary() {
        return Subscription.getSubscriptionSummary();
    }
}
