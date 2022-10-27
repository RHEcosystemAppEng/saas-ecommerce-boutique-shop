package org.acme.saas.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.response.Response;
import org.acme.saas.model.Request;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.data.RegisterData;
import org.acme.saas.model.data.TokenData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.draft.SubscriptionDraft;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.repository.RequestRepository;
import org.acme.saas.repository.SubscriptionRepository;
import org.acme.saas.repository.TenantRepository;
import org.acme.saas.restclient.RulesClient;
import org.acme.saas.service.RulesClientStub;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.acme.saas.model.mappers.RequestMapper;
import org.acme.saas.util.CommonUtil;
import org.hamcrest.Matchers;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.acme.saas.util.CommonUtil.createNewTenant;
import static org.acme.saas.util.CommonUtil.getDummyRegisterData;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class TenantResourceTest {

    private static final Logger LOG = Logger.getLogger(TenantResourceTest.class);

    @InjectMock
    @RestClient
    RulesClient rulesClient;

    @Inject
    TenantRepository tenantRepository;

    @Inject
    RequestRepository requestRepository;

    @Inject
    SubscriptionRepository subscriptionRepository;

    @BeforeEach
    void mockRestClient() {
        RulesClientStub.initMock(rulesClient);
    }

    @Test
    void signUpNewTenant() {

        RegisterData dummyRegisterData = getDummyRegisterData();
        Response response = createNewTenant(dummyRegisterData);

        TokenData responseToken = response.as(TokenData.class);
        LOG.debugf("Response is %s", responseToken);
        assertThat(responseToken.getKey(), Matchers.is(notNullValue()));

        assertThat(responseToken.getLoggedInUserName(), is(dummyRegisterData.getTenantName()));
        assertThat(responseToken.getId(), Matchers.is(notNullValue()));

        assertThat("Tenant record is not persisted in the database",
                Objects.nonNull(Tenant.findByTenantKey(responseToken.getKey()).await().indefinitely()));
        assertThat("Request record is not persisted in the database",
                Objects.nonNull(Request.findAllByTenantKey(responseToken.getKey()).await().indefinitely()));
        assertThat("Subscription record is not persisted in the database",
                Objects.nonNull(Subscription.findAllByTenantKey(responseToken.getKey()).await().indefinitely()));
    }

    @Test
    void loginTest() {
        RegisterData dummyRegisterData = getDummyRegisterData();
        Response createTenantResponse = createNewTenant(dummyRegisterData);
        TokenData createResponseToken = createTenantResponse.as(TokenData.class);
        LOG.debugf("Response is %s", createResponseToken);
        assertThat(createResponseToken.getKey(), Matchers.is(notNullValue()));

        LoginData loginData = new LoginData();
        loginData.setEmail(dummyRegisterData.getEmail());
        loginData.setPassword(dummyRegisterData.getPassword());

        Response loginResponse = given()
                .when().contentType("application/json")
                .body(loginData)
                .post("/tenant/login")
                .then()
                .statusCode(200)
                .extract().response();
        TokenData responseToken = loginResponse.as(TokenData.class);
        LOG.debugf("Response is %s", responseToken);
        assertThat(responseToken.getKey(), Matchers.is(notNullValue()));
        assertThat(responseToken.getLoggedInUserName(), Matchers.is(dummyRegisterData.getTenantName()));
    }


    @Test
    public void testMapper() {
        Request request = new Request();
        request.id = 12L;
        request.tenantKey = "abc";

        RequestDraft requestDraft = RequestMapper.INSTANCE.requestToRequestDraft(request);
        assertThat(requestDraft.getId(), Matchers.is(12L));
        assertThat(requestDraft.getTenantKey(), Matchers.is("abc"));
    }

    @Test
    void getTenantByIdTest() {

        RegisterData dummyRegisterData = getDummyRegisterData();
        Response createTenantResponse = createNewTenant(dummyRegisterData);

        TokenData responseToken = createTenantResponse.as(TokenData.class);
        LOG.debugf("Response is %s", responseToken);
        assertThat(responseToken.getKey(), Matchers.is(notNullValue()));

        Response fetchTenantResponse = given()
                .when().contentType("application/json")
                .get("/tenant/" + responseToken.getKey())
                .then()
                .statusCode(200)
                .extract().response();

        TenantDraft responseTenantDraft = fetchTenantResponse.as(TenantDraft.class);
        LOG.debugf("Response is %s", responseTenantDraft);
        assertThat(responseTenantDraft.getTenantKey(), is(responseToken.getKey()));
        assertThat(responseTenantDraft.getSubscriptions().size(), is(1));

        SubscriptionDraft subscriptionDraft = responseTenantDraft.getSubscriptions().get(0);
        LOG.debugf("Got SubscriptionDraft %s", subscriptionDraft);
        assertThat(subscriptionDraft.getUrl(), is("TESTURL"));

        RequestDraft requestDraft = subscriptionDraft.getRequest();
        LOG.debugf("Got RequestDraft %s", requestDraft);
        assertThat(requestDraft.getId(), is(not(0L)));
    }

    @BeforeEach
    public void tearDown() {
        CommonUtil.tearDown();
    }
}