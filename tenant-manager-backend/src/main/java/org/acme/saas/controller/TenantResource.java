package org.acme.saas.controller;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.acme.saas.common.Constants;
import org.acme.saas.model.Request;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.data.RegisterData;
import org.acme.saas.model.data.TenantData;
import org.acme.saas.model.data.TokenData;
import org.acme.saas.model.data.TokenData.TokenDataBuilder;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.draft.RequestDraft.RequestDraftBuilder;
import org.acme.saas.model.draft.StyleDraft;
import org.acme.saas.model.draft.StyleDraft.StyleDraftBuilder;
import org.acme.saas.model.draft.SubscriptionDraft;
import org.acme.saas.model.draft.SubscriptionDraft.SubscriptionDraftBuilder;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.draft.TenantDraft.TenantDraftBuilder;
import org.acme.saas.model.mappers.RequestMapper;
import org.acme.saas.model.mappers.TenantMapper;
import org.acme.saas.service.ProvisionService;
import org.acme.saas.service.StyleService;
import org.acme.saas.service.SubscriptionService;
import org.acme.saas.service.TenantService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.function.Function;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/tenant")
public class TenantResource {

    @Inject
    TenantService tenantService;

    @Inject
    ProvisionService provisionService;

    @Inject
    SubscriptionService subscriptionService;

    @Inject
    StyleService styleService;

    @Operation(summary = "Health check service")
    @GET
    @Path("/health")
    @Produces(APPLICATION_JSON)
    public Uni<String> healthEndpoint() {
        Log.info("In Tenant Resource = healthEndpoint");
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
    public Uni<TenantDraft> getTenantById(@PathParam("tenantKey") String tenantKey) {
        return tenantService.findByTenantKey(tenantKey)
                .onItem().transform(TenantMapper.INSTANCE::tenantToTenantDraft)
                .onItem().ifNull().failWith(RuntimeException::new);
    }

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    public Uni<List<TenantData>> getAllTenants() {
        Log.info("In Tenant Resource = getAllTenants");
        return tenantService.findAllTenants();
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
//        tenantDraftBuilder.desiredState(false);
        tenantDraftBuilder.status(Constants.TENANT_STATUS_RUNNING);

        RequestDraftBuilder requestDraftBuilder = RequestDraft.builder();
        requestDraftBuilder.tenantKey(tenantKey);
        requestDraftBuilder.hostName(registerData.getHostName().length() == 0 ? tenantKey : registerData.getHostName());
        requestDraftBuilder.serviceName(Constants.REQUEST_SERVICE_NAME_ALL);
        requestDraftBuilder.tier(registerData.getTier());
        requestDraftBuilder.avgConcurrentShoppers(registerData.getAvgConcurrentShoppers());
        requestDraftBuilder.peakConcurrentShoppers(registerData.getPeakConcurrentShoppers());
        requestDraftBuilder.fromTime(registerData.getFromTime());
        requestDraftBuilder.toTime(registerData.getToTime());
        requestDraftBuilder.status(Constants.REQUEST_STATUS_APPROVED);
        RequestDraft requestDraft = requestDraftBuilder.build();

        SubscriptionDraftBuilder subscriptionDraftBuilder = SubscriptionDraft.builder();
        subscriptionDraftBuilder.tenantKey(tenantKey);
        subscriptionDraftBuilder.serviceName(Constants.REQUEST_SERVICE_NAME_ALL);
        subscriptionDraftBuilder.tier(registerData.getTier());
        subscriptionDraftBuilder.status(Constants.SUBSCRIPTION_STATUS_INITIAL);

        StyleDraftBuilder styleDraftBuilder = StyleDraft.builder();
        styleDraftBuilder.tenantKey(tenantKey);
        styleDraftBuilder.headingText(registerData.getHeadingText());
        styleDraftBuilder.headingColor(registerData.getHeadingColor());
        styleDraftBuilder.ribbonColor(registerData.getRibbonColor());

        return subscriptionService.calculateInstanceCount(registerData.getTier(), registerData.getAvgConcurrentShoppers())
                .onItem().transform(
                        instanceCount -> subscriptionDraftBuilder.minInstanceCount(instanceCount[0])
                                .maxInstanceCount(instanceCount[1])

                ).onItem().transformToUni(
                        ignore -> Uni.combine().all().unis(
                                        styleService.createNewStyle(styleDraftBuilder.build()),
                                        subscriptionService.createNewSubscription(
                                                tenantDraftBuilder.build(), subscriptionDraftBuilder.build(), requestDraft)
                                ).combinedWith((style, subscription) -> tenantService.createNewTenant(tenantDraftBuilder.build(),
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

    @Operation(summary = "Enables a tenant subscription")
    @APIResponse(responseCode = "200", description = "Tenant enabled", content =
    @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = TenantData.class)))
    @APIResponse(responseCode = "404", description = "No tenant was found for the given tenantKey")
    @APIResponse(responseCode = "400", description = "The tenant is not in the expected Waiting state")
    @PUT
    @Path("/{tenantKey}/enable")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TenantData> enableTenant(@Parameter(description = "tenantKey of the tenant to be enabled", required
            = true) @PathParam("tenantKey") String tenantKey) {
        return Tenant.findByTenantKey(tenantKey).onItem()
                .ifNotNull().transform(Unchecked.function(tenant -> {
                            if (!tenant.status.equals(Constants.TENANT_STATUS_REQUESTED) &&
                                    !tenant.status.equals(Constants.TENANT_STATUS_STOPPED)) {
                                throw new BadRequestException("The tenant is not in the expected Waiting/Stopped state");
                            } else {
                                return tenantService.updateTenantStatus(tenantKey, Constants.TENANT_STATUS_RUNNING);
                            }
                        })
                ).flatMap(Function.identity())
                .onItem().ifNull().failWith(NotFoundException::new);

    }

    @Operation(summary = "Disables a tenant subscription")
    @APIResponse(responseCode = "200", description = "Tenant disabled", content =
    @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = TenantData.class)))
    @APIResponse(responseCode = "404", description = "No tenant was found for the given tenantKey")
    @APIResponse(responseCode = "400", description = "The tenant is not in the expected Running state")
    @PUT
    @Path("/{tenantKey}/disable")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TenantData> disableTenant(@Parameter(description = "tenantKey of the tenant to be stopped", required
            = true) @PathParam("tenantKey") String tenantKey) {
        return Tenant.findByTenantKey(tenantKey).onItem()
                .ifNotNull().transform(Unchecked.function(tenant -> {
                            if (!tenant.status.equals(Constants.TENANT_STATUS_RUNNING)) {
                                throw new BadRequestException("The tenant is not in the expected Stopped state");
                            } else {
                                Request request = tenant.subscriptions.get(0).request;
                                request.avgConcurrentShoppers = 0;
                                request.peakConcurrentShoppers = 0;
                                Uni<PanacheEntityBase> updateAvgConcurrentShoppers = request.persist();

                                Uni<String> stringUni = provisionService.onPurgeSubscription(
                                        TenantMapper.INSTANCE.tenantToTenantDraft(tenant),
                                        RequestMapper.INSTANCE.requestToRequestDraft(tenant.subscriptions.get(0).request)
                                );
                                return Uni.combine().all().unis(updateAvgConcurrentShoppers, stringUni)
                                        .combinedWith((panacheEntityBase, s) ->
                                                tenantService.updateTenantStatus(tenantKey, Constants.TENANT_STATUS_STOPPED))
                                        .flatMap(Function.identity());
                            }
                        })
                ).flatMap(Function.identity())
                .onItem().ifNull().failWith(NotFoundException::new);

    }

    @Operation(summary = "Purges a tenant subscription")
    @APIResponse(responseCode = "200", description = "Tenant purged", content =
    @Content(mediaType =
            APPLICATION_JSON, schema =
    @Schema(implementation = TenantData.class)))
    @APIResponse(responseCode = "404", description = "No tenant was found for the given tenantKey")
    @APIResponse(responseCode = "400", description = "The tenant is not in the expected Running/Stopped state")
    @PUT
    @Path("/{tenantKey}/purge")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TenantData> purgeTenant(@Parameter(description = "tenantKey of the tenant to be purged", required
            = true) @PathParam("tenantKey") String tenantKey) {
        return Tenant.findByTenantKey(tenantKey).onItem()
                .ifNotNull().transform(Unchecked.function(tenant -> {
                            if (!tenant.status.equals(Constants.TENANT_STATUS_RUNNING) &&
                                    !tenant.status.equals(Constants.TENANT_STATUS_STOPPED)) {
                                throw new BadRequestException("The tenant is not in the expected Running/Stopped state");
                            } else {
                                Request request = tenant.subscriptions.get(0).request;
                                request.avgConcurrentShoppers = 0;
                                request.peakConcurrentShoppers = 0;
                                Uni<PanacheEntityBase> updateAvgConcurrentShoppers = request.persist();

                                Uni<String> stringUni = provisionService.onPurgeSubscription(
                                        TenantMapper.INSTANCE.tenantToTenantDraft(tenant),
                                        RequestMapper.INSTANCE.requestToRequestDraft(tenant.subscriptions.get(0).request)
                                );
                                return Uni.combine().all().unis(updateAvgConcurrentShoppers, stringUni)
                                        .combinedWith((panacheEntityBase, s) ->
                                                tenantService.updateTenantStatus(tenantKey, Constants.TENANT_STATUS_PURGED))
                                        .flatMap(Function.identity());
                            }
                        })
                ).flatMap(Function.identity())
                .onItem().ifNull().failWith(NotFoundException::new);

    }
}
