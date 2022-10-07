package org.acme.saas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.draft.TokenData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.jboss.logging.Logger;

@QuarkusTest
public class TenantResourceTest {
    private static final Logger LOG = Logger.getLogger(TenantResourceTest.class);
    @Test
    public void testDispatchedEvent() throws JsonProcessingException {
        TenantDraft tenant=new TenantDraft();
        tenant.setTenantKey("AAA");
        tenant.setEmail("aaa@mail.com");
        tenant.setTenantUserName("username");
        tenant.setPassword("123456");

        Response response=RestAssured.given().contentType("application/json")
                .body(tenant)
                .post("/tenant/signup")
                .then().statusCode(200).extract().response();

        TokenData responseToken=response.as(TokenData.class);
        LOG.infof("Response is %s",responseToken);
        assertThat(responseToken.getKey(), is(tenant.getTenantKey()));
        assertThat(responseToken.getLoggedInUserName(), is(tenant.getTenantUserName()));
        assertThat(responseToken.getId(), is(notNullValue()));
    }
}