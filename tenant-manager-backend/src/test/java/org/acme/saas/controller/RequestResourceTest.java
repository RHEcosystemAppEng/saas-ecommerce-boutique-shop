package org.acme.saas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.acme.saas.model.data.RegisterData;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.model.data.RequestData;
import org.acme.saas.model.data.TokenData;
import org.acme.saas.util.CommonUtil;
import org.hamcrest.Matchers;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.acme.saas.util.CommonUtil.createNewTenant;
import static org.acme.saas.util.CommonUtil.getDummyRegisterData;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class RequestResourceTest {

    private static final Logger LOG = Logger.getLogger(RequestResourceTest.class);

    @Test
    void getPendingRequestsTest() {
        RegisterData dummyRegisterData = getDummyRegisterData();
        Response createTenantResponse = createNewTenant(dummyRegisterData);
        TokenData createResponseToken = createTenantResponse.as(TokenData.class);
        LOG.debugf("Response is %s", createResponseToken);
        assertThat(createResponseToken.getKey(), Matchers.is(notNullValue()));

        RequestData requestData = new RequestData();
        requestData.setTenantKey(createResponseToken.getKey());
        requestData.setAvgConcurrentShoppers(50);
        requestData.setPeakConcurrentShoppers(100);
        requestData.setHostName("test-host-name");

        Response createRequestResponse = given()
                .when().contentType("application/json")
                .body(requestData)
                .post("/request/resource")
                .then()
                .statusCode(200)
                .extract().response();
        TokenData createRequestResponseToken = createRequestResponse.as(TokenData.class);
        LOG.debugf("Response is %s", createRequestResponseToken);
        assertThat(createRequestResponseToken.getKey(), Matchers.is(notNullValue()));
        assertThat(createRequestResponseToken.getLoggedInUserName(), Matchers.is(dummyRegisterData.getTenantName()));

        Response getPendingRequestResponse = given()
                .when().contentType("application/json")
                .get("/request/pending")
                .then()
                .statusCode(200)
                .extract().response();

        List rawList = getPendingRequestResponse.as(List.class);
        List<RequestChangeData> requestChangeDataList = new ArrayList<>();
        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
        for (Object raw : rawList) {
            final RequestChangeData requestChangeData = mapper.convertValue(raw, RequestChangeData.class);
            requestChangeDataList.add(requestChangeData);
        }
        assertEquals(requestChangeDataList.size(), 1);
        assertEquals(1, requestChangeDataList.get(0).getNewMinInstances());
    }

    @Test
    void createNewRequestTest() {
        RegisterData dummyRegisterData = getDummyRegisterData();
        Response createTenantResponse = createNewTenant(dummyRegisterData);
        TokenData createResponseToken = createTenantResponse.as(TokenData.class);
        LOG.debugf("Response is %s", createResponseToken);
        assertThat(createResponseToken.getKey(), Matchers.is(notNullValue()));

        RequestData requestData = new RequestData();
        requestData.setTenantKey(createResponseToken.getKey());
        requestData.setAvgConcurrentShoppers(50);
        requestData.setPeakConcurrentShoppers(100);
        requestData.setHostName("test-host-name");

        Response response = given()
                .when().contentType("application/json")
                .body(requestData)
                .post("/request/resource")
                .then()
                .statusCode(200)
                .extract().response();
        TokenData responseToken = response.as(TokenData.class);
        LOG.debugf("Response is %s", responseToken);
        assertThat(responseToken.getKey(), Matchers.is(notNullValue()));
        assertThat(responseToken.getLoggedInUserName(), Matchers.is(dummyRegisterData.getTenantName()));
    }

    @BeforeEach
    public void tearDown() {
        CommonUtil.tearDown();
    }
}