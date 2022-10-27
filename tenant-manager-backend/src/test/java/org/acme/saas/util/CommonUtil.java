package org.acme.saas.util;

import io.restassured.response.Response;
import org.acme.saas.model.Request;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.Tenant;
import org.acme.saas.model.data.RegisterData;
import org.jboss.logging.Logger;

import static io.restassured.RestAssured.given;

public class CommonUtil {
    static Logger log = Logger.getLogger(CommonUtil.class);

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

    public static void tearDown() {
        Subscription.deleteAll().subscribe().with(
                success -> log.infof("Deleted %d Subscription", success),
                failure -> log.infof("Delete Subscription failure: %s", failure)
        );
        Request.deleteAll().subscribe().with(
                success -> log.infof("Deleted %d Request", success),
                failure -> log.infof("Delete Request failure: %s", failure)
        );
        Tenant.deleteAll().subscribe().with(
                success -> log.infof("Deleted %d Tenant", success),
                failure -> log.infof("Delete Tenant failure: %s", failure)
        );
    }
}
