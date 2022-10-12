package org.acme.saas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.acme.saas.model.data.RegisterData;
import org.acme.saas.model.data.SubscriptionSummaryData;
import org.acme.saas.model.data.TokenData;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.acme.saas.util.CommonUtil.createNewTenant;
import static org.acme.saas.util.CommonUtil.getDummyRegisterData;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SubscriptionResourceTest {

    private static final Logger LOG = Logger.getLogger(ManagerResourceTest.class);

    @Test
    void getSubscriptionSummary() {

        // First tenant
        RegisterData firstTenant = getDummyRegisterData();
        firstTenant.setTenantName("tenant-1");
        firstTenant.setEmail("first@test.com");
        firstTenant.setAvgConcurrentShoppers(10);
        firstTenant.setPeakConcurrentShoppers(100);
        firstTenant.setTier("Silver");

        Response firstTenantResponse = createNewTenant(firstTenant);
        LOG.debugf("Response is %s", firstTenantResponse);
        TokenData firstTokenData = firstTenantResponse.as(TokenData.class);
        assertNotNull(firstTokenData.getKey());
        LOG.debugf("First generated Tenant Key: %s", firstTokenData.getKey());

        // Second tenant
        RegisterData secondTenant = getDummyRegisterData();
        secondTenant.setTenantName("tenant-2");
        secondTenant.setEmail("second@test.com");
        secondTenant.setAvgConcurrentShoppers(20);
        secondTenant.setPeakConcurrentShoppers(200);
        secondTenant.setTier("Silver");

        Response secondTenantResponse = createNewTenant(secondTenant);
        LOG.debugf("Response is %s", secondTenantResponse);
        TokenData secondTokenData = secondTenantResponse.as(TokenData.class);
        assertNotNull(secondTokenData.getKey());
        LOG.debugf("Second generated Tenant Key: %s", secondTokenData.getKey());

        // Third tenant
        RegisterData thirdTenant = getDummyRegisterData();
        thirdTenant.setTenantName("tenant-3");
        thirdTenant.setEmail("third@test.com");
        thirdTenant.setAvgConcurrentShoppers(30);
        thirdTenant.setPeakConcurrentShoppers(300);
        thirdTenant.setTier("Gold");

        Response thirdTenantResponse = createNewTenant(thirdTenant);
        LOG.debugf("Response is %s", thirdTenantResponse);
        TokenData thirdTokenData = thirdTenantResponse.as(TokenData.class);
        assertNotNull(thirdTokenData.getKey());
        LOG.debugf("Third generated Tenant Key: %s", thirdTokenData.getKey());

        Response summaryResponse = given()
                .when().contentType("application/json")
                .get("/subscribe/summary")
                .then()
                .statusCode(200)
                .extract().response();

        List rawList = summaryResponse.as(List.class);
        List<SubscriptionSummaryData> subscriptionSummaryDataList = new ArrayList<>();
        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
        for (Object raw : rawList) {
            final SubscriptionSummaryData subscriptionSummaryData = mapper.convertValue(raw, SubscriptionSummaryData.class);
            subscriptionSummaryDataList.add(subscriptionSummaryData);
        }
        assertEquals(subscriptionSummaryDataList.size(), 2);
        assertTrue(subscriptionSummaryDataList.stream()
                .anyMatch(s -> s.getTier().equals("Silver") &&
                        s.getTotalSubscriptions() == 2 &&
                        s.getAggregatedShoppers() == 30));
        assertTrue(subscriptionSummaryDataList.stream()
                .anyMatch(s -> s.getTier().equals("Gold") &&
                        s.getTotalSubscriptions() == 1 &&
                        s.getAggregatedShoppers() == 30));
    }
}