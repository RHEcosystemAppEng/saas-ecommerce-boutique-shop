package org.acme.saas;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class ProvisionPlanTest {
    private static final Logger log = Logger.getLogger(ProvisionPlanTest.class);

    private String inputs(int averageConcurrentShoppers, int peakConcurrentShoppers) {
        return String.format("{ \"Average Concurrent Shoppers\": %d, \"Peak Concurrent Shoppers\": %d }",
                averageConcurrentShoppers, peakConcurrentShoppers);
    }

    @ParameterizedTest
    @ArgumentsSource(ProvisionPlanArgumentsProvider.class)
    public void testProvisionPlan(int averageConcurrentShoppers, int peakConcurrentShoppers,
                                  Map<String, Integer> expectedReplicas) {
        Response response = given()
                .body(inputs(averageConcurrentShoppers, peakConcurrentShoppers))
                .contentType(ContentType.JSON)
                .when()
                .post("/ProvisionPlan")
                .then()
                .statusCode(200).extract().response();

        Map<String, Object> responseMap = response.as(HashMap.class);
        Map<String, Integer> actualReplicas = (Map<String, Integer>) responseMap.get("Compute Replicas");
        log.infof("Comparing expected %s to actual %s for %s/%d", expectedReplicas, actualReplicas,
                averageConcurrentShoppers, peakConcurrentShoppers);
        assertThat(actualReplicas, is(expectedReplicas));
    }

    static class ProvisionPlanArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(10, 100, Map.of("minReplicas", 0, "maxReplicas", 2)),
                    Arguments.of(100, 1000, Map.of("minReplicas", 2, "maxReplicas", 20)));
        }
    }
}