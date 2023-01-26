package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.common.Constants;
import org.acme.saas.model.Request;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.mappers.RequestMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.acme.saas.common.Constants.REQUEST_STATUS_APPROVED;
import static org.acme.saas.common.Constants.REQUEST_STATUS_PENDING;
import static org.acme.saas.common.Constants.REQUEST_STATUS_REJECTED;

@ApplicationScoped
public class RequestService {
    private Logger log = Logger.getLogger(RequestService.class);

    @Inject
    ProvisionService provisionService;

    @ReactiveTransactional
    public Uni<Request> createNewRequest(RequestDraft requestDraft) {
        Request request = RequestMapper.INSTANCE.requestDraftToRequest(requestDraft);
        return request.persist();
    }

    @Inject
    SubscriptionService subscriptionService;

    @ReactiveTransactional
    public Uni<List<RequestChangeData>> getRequestChangeData() {
        return Uni.combine().all().unis(
                Request.findAllPendingRequests(),
                Tenant.findAllTenantsByStatus(Constants.TENANT_STATUS_RUNNING)
        ).combinedWith((pendingRequests, tenants) -> {
            List<RequestChangeData> data = new ArrayList<>();
            Map<String, List<Tenant>> tenantMap = tenants.stream()
                    .collect(Collectors.groupingBy(tenant -> tenant.tenantKey));

            for (Request request : pendingRequests) {
                int[] instanceCount = subscriptionService.calculateInstanceCount(request.avgConcurrentShoppers,
                        request.peakConcurrentShoppers);

                RequestChangeData changeData = new RequestChangeData();
                changeData.setRequestId(request.id);
                changeData.setTenantKey(request.tenantKey);
                changeData.setNewTier(request.tier);
                changeData.setServiceName(request.serviceName);
                changeData.setNewMinInstances(instanceCount[0]);
                changeData.setNewMaxInstances(instanceCount[1]);

                Tenant tenant = tenantMap.get(request.tenantKey).get(0);
                changeData.setTenantName(tenant.tenantName);

                tenant.subscriptions.stream().limit(1)
                        .forEach(subscription -> {
                            changeData.setCurrentTier(subscription.tier);
                            changeData.setOldMinInstances(subscription.minInstanceCount);
                            changeData.setOldMaxInstances(subscription.maxInstanceCount);
                        });

                data.add(changeData);
            }
            return data;
        });
    }

    @ReactiveTransactional
    public Uni<RequestDraft> approveByRequestId(long id) {
        Uni<Request> requestUni = Request.findById(id);

        // @todo remove the status change and let the scheduler to handle it
        return requestUni.onItem().ifNotNull().transformToUni(request -> {
                            validateRequestStatus(request);
                            request.status = REQUEST_STATUS_APPROVED;
                            Uni<Request> updatedRequestUni = request.persist();
                            Uni<Tenant> tenantUni = Tenant.findByTenantKey(request.tenantKey);
                            Uni<Subscription> subscriptionUni = Subscription.findFirstByTenantKey(request.tenantKey);

                            return Uni.combine().all().unis(updatedRequestUni, tenantUni, subscriptionUni).combinedWith(
                                    (updatedRequest, tenant, subscription) -> {
                                        subscription.request = updatedRequest;
                                        Uni<Subscription> updatedSubscriptionUni = subscription.persist();

                                        int[] replicas = subscriptionService.calculateInstanceCount(
                                                request.avgConcurrentShoppers,
                                                request.peakConcurrentShoppers);
                                        Uni<String> resourceUpdateUni =
                                                provisionService.onResourceUpdate(tenant.tenantName,
                                                        replicas[0],
                                                        replicas[1]);

                                        return Uni.combine().all().unis(updatedSubscriptionUni, resourceUpdateUni).combinedWith(
                                                (updatedSubscription, resourceUpdate) ->
                                                        // This is necessary to move back the execution into one event
                                                        // thread, otherwise it takes the worker thread of the ProvisionService
                                                        Panache.withTransaction(() ->
                                                                Uni.createFrom().item(RequestMapper.INSTANCE.requestToRequestDraft((
                                                                        updatedSubscription).request))
                                                        )
                                        );
                                    });
                        }
                ).onItem().ifNull().failWith(NotFoundException::new)
                .flatMap(Function.identity())
                .flatMap(Function.identity());
    }

    private void validateRequestStatus(Request request) {
        if (!Objects.equals(request.status, Constants.REQUEST_STATUS_PENDING)) {
            log.warnf("Wrong status %s, %s was expected", request.status, REQUEST_STATUS_PENDING);
            throw new BadRequestException(String.format("Wrong status %s, %s was expected", request.status,
                    REQUEST_STATUS_PENDING));
        }
    }

    @ReactiveTransactional
    public Uni<RequestDraft> rejectByRequestId(long id) {
        Uni<Request> requestUni = Request.findById(id);
        return requestUni.onItem().ifNotNull().transformToUni(request -> {
            validateRequestStatus(request);
            request.status = REQUEST_STATUS_REJECTED;
            Uni<Request> updatedRequestUni = request.persist();
            return updatedRequestUni.onItem().transform(RequestMapper.INSTANCE::requestToRequestDraft
            );
        }).onItem().ifNull().failWith(NotFoundException::new);
    }
}
