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
import java.util.Objects;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class CostComputationTest {
    private static final Logger log = Logger.getLogger(CostComputationTest.class);

    private String inputs(String tier, int averageConcurrentShoppers) {
        return String.format("{ \"tier\": \"%s\", \"averageConcurrentShoppers\": %d }", tier,
                averageConcurrentShoppers);
    }

    @ParameterizedTest
    @ArgumentsSource(CostComputationArgumentsProvider.class)
    public void testComputedCosts(String tier, int averageConcurrentShoppers, Double expectedCost) {
        Response response = given()
                .body(inputs(tier, averageConcurrentShoppers))
                .contentType(ContentType.JSON)
                .when()
                .post("/CostComputation")
                .then()
                .statusCode(200).extract().response();

        Map<String, Object> responseMap = response.as(HashMap.class);
        // Pay attention that numbers might be reported as integers so you need to pass from the generic Number class
        // to get the Double
        Double actualCost = Objects.isNull(responseMap.get("calculatedPrice")) ? null : ((Number) responseMap.get(
                "calculatedPrice")).doubleValue();
        log.infof("Comparing expected %s to actual %s for %s/%d", expectedCost, actualCost,
                tier, averageConcurrentShoppers);
        assertThat(actualCost, is(expectedCost));
    }

    static class CostComputationArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("Free", 100, 0.0),
                    Arguments.of("Silver", 1000, 100.0),
                    Arguments.of("Gold", 1000, 200.0),
                    Arguments.of("Platinum", 1000, 300.0),
                    Arguments.of("Custom", 500, (Double) null));
        }
    }
}