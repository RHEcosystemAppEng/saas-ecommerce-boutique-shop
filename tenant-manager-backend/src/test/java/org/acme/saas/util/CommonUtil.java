package org.acme.saas.util;

import io.restassured.response.Response;
import org.acme.saas.model.data.RegisterData;

import static io.restassured.RestAssured.given;

public class CommonUtil {

    public static RegisterData getDummyRegisterData() {
        RegisterData registerData = new RegisterData();
        registerData.setTenantName("redhat-customer");
        registerData.setEmail("john@gmail.com");
        registerData.setContactName("John");
        registerData.setPassword("123");
        registerData.setTier("Silver");
        registerData.setHostName("test-domain");
        registerData.setAvgConcurrentShoppers(500);
        registerData.setPeakConcurrentShoppers(1000);

        return registerData;
    }


    public static Response createNewTenant(RegisterData registerData) {
        return given()
                .when().contentType("application/json").body(registerData)
                .post("/tenant/signup")
                .then()
                .statusCode(200)
                .extract().response();
    }
}
