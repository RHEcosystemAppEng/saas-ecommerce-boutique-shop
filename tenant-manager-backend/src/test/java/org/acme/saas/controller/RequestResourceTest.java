package org.acme.saas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.acme.saas.common.Constants;
import org.acme.saas.model.data.RegisterData;
import org.acme.saas.model.data.RequestChangeData;
import org.acme.saas.model.data.RequestData;
import org.acme.saas.model.data.TokenData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.service.RequestService;
import org.acme.saas.util.CommonUtil;
import org.hamcrest.Matchers;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.acme.saas.util.CommonUtil.createNewTenant;
import static org.acme.saas.util.CommonUtil.getDummyRegisterData;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class RequestResourceTest {

    private static final Logger LOG = Logger.getLogger(RequestResourceTest.class);

    // @Test
    // void getPendingRequestsTest() {
    //     RegisterData dummyRegisterData = getDummyRegisterData();
    //     Response createTenantResponse = createNewTenant(dummyRegisterData);
    //     TokenData createResponseToken = createTenantResponse.as(TokenData.class);
    //     LOG.debugf("Response is %s", createResponseToken);
    //     assertThat(createResponseToken.getKey(), Matchers.is(notNullValue()));

    //     RequestData requestData = new RequestData();
    //     requestData.setTenantKey(createResponseToken.getKey());
    //     requestData.setAvgConcurrentShoppers(50);
    //     requestData.setPeakConcurrentShoppers(100);
    //     requestData.setHostName("test-host-name");

    //     Response createRequestResponse = given()
    //             .when().contentType("application/json")
    //             .body(requestData)
    //             .post("/request/resource")
    //             .then()
    //             .statusCode(200)
    //             .extract().response();
    //     TokenData createRequestResponseToken = createRequestResponse.as(TokenData.class);
    //     LOG.debugf("Response is %s", createRequestResponseToken);
    //     assertThat(createRequestResponseToken.getKey(), Matchers.is(notNullValue()));
    //     assertThat(createRequestResponseToken.getLoggedInUserName(), Matchers.is(dummyRegisterData.getTenantName()));

    //     List<RequestChangeData> requestChangeDataList = getPendingChangeData();
    //     assertEquals(requestChangeDataList.size(), 1);
    //     assertEquals(1, requestChangeDataList.get(0).getNewMinInstances());
    // }

    // private List<RequestChangeData> getPendingChangeData() {
    //     Response getPendingRequestResponse = given()
    //             .when().contentType("application/json")
    //             .get("/request/pending")
    //             .then()
    //             .statusCode(200)
    //             .extract().response();

    //     List rawList = getPendingRequestResponse.as(List.class);
    //     List<RequestChangeData> requestChangeDataList = new ArrayList<>();
    //     final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
    //     for (Object raw : rawList) {
    //         final RequestChangeData requestChangeData = mapper.convertValue(raw, RequestChangeData.class);
    //         requestChangeDataList.add(requestChangeData);
    //     }
    //     return requestChangeDataList;
    // }

    // @Test
    // void createNewRequestTest() {
    //     TokenData responseToken = createNewRequest();
    //     LOG.debugf("Response is %s", responseToken);
    //     assertThat(responseToken.getKey(), Matchers.is(notNullValue()));
    //     assertThat(responseToken.getLoggedInUserName(), Matchers.is(responseToken.getLoggedInUserName()));
    // }

    // private TokenData createNewRequest() {
    //     RegisterData dummyRegisterData = getDummyRegisterData();
    //     Response createTenantResponse = createNewTenant(dummyRegisterData);
    //     TokenData createResponseToken = createTenantResponse.as(TokenData.class);
    //     LOG.debugf("Response is %s", createResponseToken);
    //     assertThat(createResponseToken.getKey(), Matchers.is(notNullValue()));

    //     return createNewRequest(createResponseToken.getKey());
    // }

    // private TokenData createNewRequest(String tenantKey) {
    //     RequestData requestData = new RequestData();
    //     requestData.setTenantKey(tenantKey);
    //     requestData.setAvgConcurrentShoppers(50);
    //     requestData.setPeakConcurrentShoppers(100);
    //     requestData.setHostName("test-host-name");

    //     Response response = given()
    //             .when().contentType("application/json")
    //             .body(requestData)
    //             .post("/request/resource")
    //             .then()
    //             .statusCode(200)
    //             .extract().response();
    //     return response.as(TokenData.class);
    // }

    // @Test
    // void approveRequest() {
    //     createNewRequest();
    //     RequestChangeData requestChangeData = getPendingChangeData().get(0);
    //     LOG.debugf("RequestChangeData is %s", requestChangeData);
    //     Response response = given()
    //             .when()
    //             .put("/request/{id}/approve", requestChangeData.getRequestId())
    //             .then()
    //             .statusCode(200)
    //             .extract().response();
    //     RequestDraft requestDraft = response.as(RequestDraft.class);
    //     LOG.debugf("RequestDraft is %s", requestDraft);
    //     assertEquals(requestDraft.getStatus(), Constants.REQUEST_STATUS_APPROVED);
    // }

    // @Test
    // void rejectRequest() {
    //     createNewRequest();
    //     RequestChangeData requestChangeData = getPendingChangeData().get(0);
    //     LOG.debugf("RequestChangeData is %s", requestChangeData);
    //     Response response = given()
    //             .when()
    //             .put("/request/{id}/reject", requestChangeData.getRequestId())
    //             .then()
    //             .statusCode(200)
    //             .extract().response();
    //     RequestDraft requestDraft = response.as(RequestDraft.class);
    //     LOG.debugf("RequestDraft is %s", requestDraft);
    //     assertEquals(requestDraft.getStatus(), Constants.REQUEST_STATUS_REJECTED);
    // }

    // @Inject
    // RequestService requestService;

    // @Test
    // public void approveOrRejectNonExistingRequest() {
    //     int notFound = RestResponse.Status.NOT_FOUND.getStatusCode();
    //     long requestId = 123L;
    //     Response response = given()
    //             .when()
    //             .put("/request/{id}/approve", requestId)
    //             .then()
    //             .statusCode(notFound)
    //             .extract().response();
    //     assertThat(response.statusCode(), is(notFound));

    //     response = given()
    //             .when()
    //             .put("/request/{id}/reject", requestId)
    //             .then()
    //             .statusCode(notFound)
    //             .extract().response();
    //     assertThat(response.statusCode(), is(notFound));
    // }

    // @Test
    // public void approveOrRejectNonPendingRequest() {
    //     int badRequest = RestResponse.Status.BAD_REQUEST.getStatusCode();
    //     createNewRequest();
    //     long requestId = 1L;
    //     Response response = given()
    //             .when()
    //             .put("/request/{id}/approve", requestId)
    //             .then()
    //             .statusCode(badRequest)
    //             .extract().response();
    //     assertThat(response.statusCode(), is(badRequest));

    //     response = given()
    //             .when()
    //             .put("/request/{id}/reject", requestId)
    //             .then()
    //             .statusCode(badRequest)
    //             .extract().response();
    //     assertThat(response.statusCode(), is(badRequest));
    // }

    // @Test
    // public void approveOrRejectNonExistingTenant() {
    //     int notFound = RestResponse.Status.NOT_FOUND.getStatusCode();
    //     RequestData requestData = new RequestData();
    //     requestData.setTenantKey("missing");
    //     requestData.setAvgConcurrentShoppers(50);
    //     requestData.setPeakConcurrentShoppers(100);
    //     requestData.setHostName("test-host-name");

    //     Response response = given()
    //             .when().contentType("application/json")
    //             .body(requestData)
    //             .post("/request/resource")
    //             .then()
    //             .statusCode(notFound)
    //             .extract().response();
    //     assertThat(response.statusCode(), is(notFound));
    // }

    @BeforeEach
    public void tearDown() {
        CommonUtil.tearDown();
    }
}