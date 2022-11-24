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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class NamespaceByTierTest {
        private static final Logger log = Logger.getLogger(HpaComputationTest.class);

        private String input(String tier, String ms) {
                return String.format("{ \"Tier\": \"%s\", \"Microservice\": \"%s\" }", tier, ms);
        }

        @ParameterizedTest
        @ArgumentsSource(NamespaceByTierArgumentsProvider.class)
        public void testNamespaceByTier(String tier, String ms,
                        Map<String, Object> expectedNamespace) {
                Response response = given()
                                .body(input(tier, ms))
                                .contentType(ContentType.JSON)
                                .when()
                                .post("/NamespaceByTier")
                                .then()
                                .statusCode(200).extract().response();

                Map<String, Object> responseMap = response.as(HashMap.class);
                Map<String, Object> actualNamespace = (Map<String, Object>) responseMap.get("Namespace By Tier");
                log.infof("Comparing expected %s to actual %s for %s/%s", expectedNamespace, actualNamespace,
                                tier, ms);
                assertThat(actualNamespace, is(expectedNamespace));
        }

        static private String[] allMicroservices = { "emailservice",
                        "paymentservice",
                        "currencyservice",
                        "checkoutservice",
                        "cartservice",
                        "shippingservice",
                        "recommendationservice",
                        "productcatalogservice",
                        "frontend",
                        "redis-cart",
                        "adservice" };

        static class NamespaceByTierArgumentsProvider implements ArgumentsProvider {
                @Override
                public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
                        List<Arguments> args = new ArrayList<>();
                        for (String ms : allMicroservices) {
                                args.add(Arguments.of("free", ms,
                                                Map.of("isTenantNamespace", false, "namespace", "boutique-free")));
                        }
                        for (String ms : allMicroservices) {
                                args.add(Arguments.of("silver", ms,
                                                Map.of("isTenantNamespace", false, "namespace", "boutique-silver")));
                        }

                        args.addAll(List.of(
                                        Arguments.of("gold", "emailservice",
                                                        Map.of("isTenantNamespace", false, "namespace",
                                                                        "enterprise-utilities")),
                                        Arguments.of("gold", "paymentservice",
                                                        Map.of("isTenantNamespace", false, "namespace",
                                                                        "enterprise-utilities")),
                                        Arguments.of("gold", "emailservice",
                                                        Map.of("isTenantNamespace", false, "namespace",
                                                                        "enterprise-utilities")),
                                        Arguments.of("gold", "checkoutservice",
                                                        Map.of("isTenantNamespace", false, "namespace",
                                                                        "boutique-ops")),
                                        Arguments.of("gold", "cartservice",
                                                        Map.of("isTenantNamespace", false, "namespace",
                                                                        "boutique-ops")),
                                        Arguments.of("gold", "shippingservice",
                                                        Map.of("isTenantNamespace", false, "namespace",
                                                                        "boutique-ops")),
                                        Arguments.of("gold", "recommendationservice",
                                                        Map.of("isTenantNamespace", false, "namespace",
                                                                        "boutique-ops")),
                                        Arguments.of("gold", "productcatalogservice",
                                                        Map.of("isTenantNamespace", false, "namespace",
                                                                        "boutique-ops")),
                                        Arguments.of("gold", "frontend",
                                                        Map.of("isTenantNamespace", true, "namespace", "")),
                                        Arguments.of("gold", "redis-cart",
                                                        Map.of("isTenantNamespace", true, "namespace", "")),
                                        Arguments.of("gold", "adservice",
                                                        Map.of("isTenantNamespace", true, "namespace", ""))));

                        for (String ms : allMicroservices) {
                                args.add(Arguments.of("premium", ms,
                                                Map.of("isTenantNamespace", true, "namespace", "")));
                        }

                        return args.stream();
                }
        }
        /*
         * emailservice
         * paymentservice
         * currencyservice
         * checkoutservice
         * cartservice
         * shippingservice
         * recommendationservice
         * productcatalogservice
         * frontend
         * redis-cart
         * adservice
         * 
         * enterprise-utilities
         * enterprise-utilities
         * enterprise-utilities
         * boutique-ops
         * boutique-ops
         * boutique-ops
         * boutique-ops
         * boutique-ops
         * TENANT_NS
         * TENANT_NS
         * TENANT_NS
         */
}