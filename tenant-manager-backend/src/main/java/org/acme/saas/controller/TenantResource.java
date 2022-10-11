package org.acme.saas.controller;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.acme.saas.common.Constants;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.data.RegisterData;
import org.acme.saas.model.data.TokenData;
import org.acme.saas.model.data.TokenData.TokenDataBuilder;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.draft.RequestDraft.RequestDraftBuilder;
import org.acme.saas.model.draft.SubscriptionDraft;
import org.acme.saas.model.draft.SubscriptionDraft.SubscriptionDraftBuilder;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.draft.TenantDraft.TenantDraftBuilder;
import org.acme.saas.model.mappers.TenantMapper;
import org.acme.saas.service.SubscriptionService;
import org.acme.saas.service.TenantService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/tenant")
public class TenantResource {
    @Inject
    TenantService tenantService;

    @Inject
    SubscriptionService subscriptionService;

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Long> test() {
        return Multi.createFrom()
                .ticks().every(Duration.ofSeconds(1))
                .onItem().transform(i -> i * 2)
                .select().first(10);

    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> healthEndpoint() {
        return Uni.createFrom().item("ok");
    }

    @GET
    @Path("/email/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Boolean> isEmailAlreadyInUse(String email) {
        return tenantService.isEmailAlreadyInUse(email);
    }

    @GET
    @Path("/{tenantKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TenantDraft> getTenantById(@PathParam("tenantKey") String tenantKey) {
        System.out.println("Inside the method");
        return tenantService.findByTenantKey(tenantKey)
                .onItem().ifNotNull().transform(TenantMapper.INSTANCE::tenantToTenantDraft)
                .onItem().ifNull().failWith(ForbiddenException::new);
//        return Uni.combine().all().unis(
//                subscriptionService.findByTenantKey(tenantKey),
//                tenantService.findByTenantKey(tenantKey)
//        ).combinedWith((subscription, tenant) -> {
//            TenantDraft draft = TenantMapper.INSTANCE.tenantToTenantDraft(tenant);
//            SubscriptionDraft subscriptionDraft = SubscriptionMapper.INSTANCE
//                    .subscriptionToSubscriptionDraft(subscription);
//            draft.setSubscriptions(List.of(subscriptionDraft));
//            return draft;
//        });

    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokenData> login(LoginData loginData) {

        return tenantService.login(loginData)
                .onItem().ifNotNull().transform(tenant -> {
                    TokenDataBuilder tokenDataBuilder = TokenData.builder();
                    tokenDataBuilder.Id(tenant.getId());
                    tokenDataBuilder.key(tenant.getTenantKey());
                    tokenDataBuilder.loggedInUserName(tenant.getTenantName());
                    return tokenDataBuilder.build();
                })
                .onItem().ifNull().failWith(() -> new NotAuthorizedException("Invalid credentials"));
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TokenData> signUpNewTenant(RegisterData registerData) {

        String tenantKey = tenantService.generateTenantKey(registerData.getTenantName());

        TenantDraftBuilder tenantDraftBuilder = TenantDraft.builder();
        tenantDraftBuilder.tenantKey(tenantKey);
        tenantDraftBuilder.tenantName(registerData.getTenantName());
        tenantDraftBuilder.email(registerData.getEmail());
        tenantDraftBuilder.password(registerData.getPassword());
        tenantDraftBuilder.orgName(registerData.getOrgName());
        tenantDraftBuilder.address(registerData.getAddress());
        tenantDraftBuilder.phone(registerData.getPhone());
        tenantDraftBuilder.contactName(registerData.getContactName());
        tenantDraftBuilder.status(Constants.TENANT_STATUS_ACTIVE);

        Uni<Tenant> savedTenantUni = tenantService.createNewTenant(tenantDraftBuilder.build());

        RequestDraftBuilder requestDraftBuilder = RequestDraft.builder();
        requestDraftBuilder.tenantKey(tenantKey);
        requestDraftBuilder.hostName(registerData.getHostName());
        requestDraftBuilder.tier(registerData.getTier());
        requestDraftBuilder.avgConcurrentShoppers(registerData.getAvgConcurrentShoppers());
        requestDraftBuilder.peakConcurrentShoppers(registerData.getPeakConcurrentShoppers());
        requestDraftBuilder.fromTime(registerData.getFromTime());
        requestDraftBuilder.toTime(registerData.getToTime());
        requestDraftBuilder.status(Constants.REQUEST_STATUS_APPROVED);
        RequestDraft requestDraft = requestDraftBuilder.build();

        int[] instanceCount = subscriptionService.calculateInstanceCount(requestDraft);
        SubscriptionDraftBuilder subscriptionDraftBuilder = SubscriptionDraft.builder();
        subscriptionDraftBuilder.tenantKey(tenantKey);
        subscriptionDraftBuilder.tier(registerData.getTier());
        subscriptionDraftBuilder.minInstanceCount(instanceCount[0]);
        subscriptionDraftBuilder.maxInstanceCount(instanceCount[1]);
        subscriptionDraftBuilder.status(Constants.SUBSCRIPTION_STATUS_INITIAL);

        Uni<Subscription> subscriptionUni = subscriptionService.createNewSubscription(
                tenantDraftBuilder.build(), subscriptionDraftBuilder.build(), requestDraft);

//        return Uni.combine().all().unis(savedTenantUni, subscriptionUni)
//                .combinedWith((tenant, subscription) -> {
//                    subscription.setTenant(tenant);
//
//                    List<Subscription> subscriptions = new ArrayList<>();
//                    subscriptions.add(subscription);
//                    return tenantService.updateTenantSubscriptions(tenant, subscriptions);
//                })
//                .flatMap(Function.identity())
//                .onItem().ifNotNull().transform(tenant -> {
//                    TokenDataBuilder tokenDataBuilder = TokenData.builder();
//                    tokenDataBuilder.Id(tenant.getId());
//                    tokenDataBuilder.key(tenant.getTenantKey());
//                    tokenDataBuilder.loggedInUserName(tenant.getTenantName());
//                    return tokenDataBuilder.build();
//                });
        return Uni.combine().all().unis(savedTenantUni, subscriptionUni)
                .combinedWith((tenant, subscription) -> {
                    TokenDataBuilder tokenDataBuilder = TokenData.builder();
                    tokenDataBuilder.Id(tenant.getId());
                    tokenDataBuilder.key(tenant.getTenantKey());
                    tokenDataBuilder.loggedInUserName(tenant.getTenantName());
                    return tokenDataBuilder.build();
                });
    }
}
