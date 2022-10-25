package org.acme.saas.controller;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.acme.saas.common.Constants;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.time.Duration;
import java.util.function.Function;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/tenant")
public class TenantResource {
    @Inject
    TenantService tenantService;

    @Inject
    SubscriptionService subscriptionService;

    @Operation(hidden = true)
    @GET
    @Path("/test")
    @Produces(APPLICATION_JSON)
    public Multi<Long> test() {
        return Multi.createFrom()
                .ticks().every(Duration.ofSeconds(1))
                .onItem().transform(i -> i * 2)
                .select().first(10);
    }

    @Operation(summary = "Health check service")
    @GET
    @Path("/health")
    @Produces(APPLICATION_JSON)
    public Uni<String> healthEndpoint() {
        return Uni.createFrom().item("ok");
    }

    @Operation(summary = "Returns a boolean to verify whether an email address is already in use")
    @APIResponse(responseCode = "200", description = "true if the given email is already in use, otherwise false")
    @GET
    @Path("/email/{email}")
    @Produces(APPLICATION_JSON)
    public Uni<Boolean> isEmailAlreadyInUse(String email) {
        return tenantService.isEmailAlreadyInUse(email);
    }

    @Operation(summary = "Returns a Tenant data given its tenantKey")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema =
    @Schema(implementation = TenantDraft.class)))
    @APIResponse(responseCode = "404", description = "No Tenant found by the given tenantKey")
    @GET
    @Path("/{tenantKey}")
    @Produces(APPLICATION_JSON)
    public Uni<TenantDraft> getTenantById(@Parameter(description = "tenantKey of the Tenant", required = true) @PathParam("tenantKey") String tenantKey) {
        return tenantService.findByTenantKey(tenantKey)
                .onItem().transform(TenantMapper.INSTANCE::tenantToTenantDraft)
                .onItem().ifNull().failWith(NotFoundException::new);
    }


    @Operation(summary = "Validates the login credentials for a user with Tenant role")
    @APIResponse(responseCode = "200", description = "Login credentials validated", content = @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = TokenData.class)))
    @APIResponse(responseCode = "401", description = "Invalid login credentials")
    @POST
    @Path("/login")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<TokenData> login(@RequestBody(
            required = true,
            content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = LoginData.class))) LoginData loginData) {

        return tenantService.login(loginData)
                .onItem().ifNotNull().transform(tenant -> {
                    TokenDataBuilder tokenDataBuilder = TokenData.builder();
                    tokenDataBuilder.Id(tenant.id);
                    tokenDataBuilder.key(tenant.tenantKey);
                    tokenDataBuilder.loggedInUserName(tenant.tenantName);
                    return tokenDataBuilder.build();
                })
                .onItem().ifNull().failWith(() -> new NotAuthorizedException("Invalid credentials"));
    }

    @Operation(summary = "Signs up a new Tenant")
    @APIResponse(responseCode = "200", description = "Tenant created and subscription request submitted", content =
    @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = TokenData.class)))
    @APIResponse(responseCode = "500", description = "Internal failure")
    @POST
    @Path("/signup")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Uni<TokenData> signUpNewTenant(@RequestBody(
            required = true,
            content = @Content(mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = RegisterData.class))) RegisterData registerData) {

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

        RequestDraftBuilder requestDraftBuilder = RequestDraft.builder();
        requestDraftBuilder.tenantKey(tenantKey);
        requestDraftBuilder.hostName(registerData.getHostName());
        requestDraftBuilder.serviceName(Constants.REQUEST_SERVICE_NAME_ALL);
        requestDraftBuilder.tier(registerData.getTier());
        requestDraftBuilder.avgConcurrentShoppers(registerData.getAvgConcurrentShoppers());
        requestDraftBuilder.peakConcurrentShoppers(registerData.getPeakConcurrentShoppers());
        requestDraftBuilder.fromTime(registerData.getFromTime());
        requestDraftBuilder.toTime(registerData.getToTime());
        requestDraftBuilder.status(Constants.REQUEST_STATUS_APPROVED);
        RequestDraft requestDraft = requestDraftBuilder.build();

        int[] instanceCount = subscriptionService.calculateInstanceCount(
                requestDraft.getAvgConcurrentShoppers(), requestDraft.getPeakConcurrentShoppers());
        SubscriptionDraftBuilder subscriptionDraftBuilder = SubscriptionDraft.builder();
        subscriptionDraftBuilder.tenantKey(tenantKey);
        subscriptionDraftBuilder.serviceName(Constants.REQUEST_SERVICE_NAME_ALL);
        subscriptionDraftBuilder.tier(registerData.getTier());
        subscriptionDraftBuilder.minInstanceCount(instanceCount[0]);
        subscriptionDraftBuilder.maxInstanceCount(instanceCount[1]);
        subscriptionDraftBuilder.status(Constants.SUBSCRIPTION_STATUS_INITIAL);

        // Added to ensure a unique Transaction
        return Panache.withTransaction(() ->
                subscriptionService.createNewSubscription(
                                tenantDraftBuilder.build(), subscriptionDraftBuilder.build(), requestDraft).onItem().ifNotNull()
                        .transform(subscription -> tenantService.createNewTenant(tenantDraftBuilder.build(),
                                subscription))
                        .flatMap(Function.identity())
                        .onItem().transform(tenant -> {
                            TokenDataBuilder tokenDataBuilder = TokenData.builder();
                            tokenDataBuilder.Id(tenant.id);
                            tokenDataBuilder.key(tenant.tenantKey);
                            tokenDataBuilder.loggedInUserName(tenant.tenantName);
                            return tokenDataBuilder.build();
                        })
        );
    }
}
