package org.acme.saas.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.acme.saas.model.data.LoginData;
import org.acme.saas.model.data.TokenData;
import org.hamcrest.Matchers;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class ManagerResourceTest {

    private static final Logger LOG = Logger.getLogger(ManagerResourceTest.class);

    @Test
    void login() {
        LoginData loginData = new LoginData();
        loginData.setEmail("admin");
        loginData.setPassword("redhat");

        Response loginResponse = given()
                .when().contentType("application/json")
                .body(loginData)
                .post("/manager/login")
                .then()
                .statusCode(200)
                .extract().response();

        TokenData responseToken = loginResponse.as(TokenData.class);
        LOG.debugf("Response is %s", responseToken);
        assertNull(responseToken.getKey());
        assertThat(responseToken.getLoggedInUserName(), Matchers.is("Tenant Manager"));
    }

}