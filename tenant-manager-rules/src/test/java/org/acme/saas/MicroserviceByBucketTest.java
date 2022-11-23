package org.acme.saas;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.List;
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
public class MicroserviceByBucketTest {
        private static final Logger log = Logger.getLogger(HpaComputationTest.class);

        private String input(String bucket) {
                return String.format("{ \"Bucket\": \"%s\" }", bucket);
        }

        @ParameterizedTest
        @ArgumentsSource(MicroserviceByBucketArgumentsProvider.class)
        public void testNamespaceByTier(String bucket, List<String> expectedMicroservices) {
                Response response = given()
                                .body(input(bucket))
                                .contentType(ContentType.JSON)
                                .when()
                                .post("/MicroserviceByBucket")
                                .then()
                                .statusCode(200).extract().response();

                Map<String, Object> responseMap = response.as(HashMap.class);
                List<String> actualMicroservices = (List<String>) responseMap.get("Microservice By Bucket");
                log.infof("Comparing expected %s to actual %s for %s", expectedMicroservices, actualMicroservices,
                                bucket);
                assertThat(actualMicroservices, is(expectedMicroservices));
        }

        static class MicroserviceByBucketArgumentsProvider implements ArgumentsProvider {
                @Override
                public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
                        return Stream.of(
                                        Arguments.of("enterprise utilities",
                                                        List.of("emailservice", "paymentservice", "currencyservice")),
                                        Arguments.of("logistics",
                                                        List.of("checkoutservice", "cartservice", "shippingservice",
                                                                        "recommendationservice",
                                                                        "productcatalogservice")),
                                        Arguments.of("shopper experience",
                                                        List.of("frontend", "redis-cart", "adservice")));
                }
        }
}