package org.acme.saas;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@QuarkusTest
public class HpaComputationTest {
        private static final Logger log = Logger.getLogger(HpaComputationTest.class);

        private String inputs(int averageConcurrentShoppers, String bucket, String tier) {
                return String.format("{ \"averageConcurrentShoppers\": %d, \"bucket\": \"%s\", \"tier\": \"%s\" }",
                                averageConcurrentShoppers, bucket, tier);
        }

        @ParameterizedTest
        @ArgumentsSource(ProvisionPlanArgumentsProvider.class)
        public void testProvisionPlan(int averageConcurrentShoppers, String bucket, String tier,
                        Map<String, Integer> expectedReplicas) {
                Response response = given()
                                .body(inputs(averageConcurrentShoppers, bucket, tier))
                                .contentType(ContentType.JSON)
                                .when()
                                .post("/HpaComputation")
                                .then()
                                .statusCode(200).extract().response();

                Map<String, Object> responseMap = response.as(HashMap.class);
                Map<String, Object> actualReplicas = (Map<String, Object>) responseMap.get("hpaReplicas");
                actualReplicas.remove("comment");
                log.infof("Comparing expected %s to actual %s for %d/%s/%s", expectedReplicas, actualReplicas,
                                averageConcurrentShoppers, bucket, tier);
                assertThat(actualReplicas, is(expectedReplicas));
        }

        static class ProvisionPlanArgumentsProvider implements ArgumentsProvider {
                @Override
                public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
                        return Stream.of(
                                        Arguments.of(1200, "enterprise utilities", "free",
                                                        Map.of("replicas", 1.2, "minReplicas", 1, "maxReplicas", 2)),
                                        Arguments.of(1200, "logistics", "free",
                                                        Map.of("replicas", 1.2, "minReplicas", 1, "maxReplicas", 2)),
                                        Arguments.of(1200, "shopper experience", "free",
                                                        Map.of("replicas", 6, "minReplicas", 5, "maxReplicas", 6)),
                                        Arguments.of(1200, "enterprise utilities", "silver",
                                                        Map.of("replicas", 2.4, "minReplicas", 2, "maxReplicas", 3)),
                                        Arguments.of(1200, "logistics", "silver",
                                                        Map.of("replicas", 2.4, "minReplicas", 2, "maxReplicas", 3)),
                                        Arguments.of(1200, "shopper experience", "silver",
                                                        Map.of("replicas", 12, "minReplicas", 10, "maxReplicas", 12)),
                                        Arguments.of(1200, "enterprise utilities", "gold",
                                                        Map.of("replicas", 2.4, "minReplicas", 2, "maxReplicas", 3)),
                                        Arguments.of(1200, "logistics", "gold",
                                                        Map.of("replicas", 2.4, "minReplicas", 2, "maxReplicas", 3)),
                                        Arguments.of(1200, "shopper experience", "gold",
                                                        Map.of("replicas", 12, "minReplicas", 10, "maxReplicas", 12)),
                                        Arguments.of(1200, "enterprise utilities", "platinum",
                                                        Map.of("replicas", 2.4, "minReplicas", 2, "maxReplicas", 3)),
                                        Arguments.of(1200, "logistics", "platinum",
                                                        Map.of("replicas", 2.4, "minReplicas", 2, "maxReplicas", 3)),
                                        Arguments.of(1200, "shopper experience", "platinum",
                                                        Map.of("replicas", 12, "minReplicas", 10, "maxReplicas", 12)));
                }
        }
}