package org.acme.saas.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.acme.saas.restclient.RulesClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.inject.Inject;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
class SubscriptionServiceTest {
    private Logger log = Logger.getLogger(RulesService.class);

    @Inject
    SubscriptionService subscriptionService;

    @InjectMock
    @RestClient
    RulesClient rulesClient;

    @BeforeEach
    void mockRestClient() {
        RulesClientStub.initMock(rulesClient);
    }


    @ParameterizedTest
    @ArgumentsSource(CalculatePriceTestArgumentsProvider.class)
    public void calculatePrice(String tier, int avgConcurrentShoppers, double expectedPrice) {
        subscriptionService.calculatePrice(tier, avgConcurrentShoppers).onItem().invoke(price -> {
            log.infof("Comparing expected price %f to actual %f for %s/%d", expectedPrice, price,
                    tier, avgConcurrentShoppers);
            assertThat(price, is(expectedPrice));
        });
    }

    @ParameterizedTest
    @ArgumentsSource(CalculateInstanceCountTestArgumentsProvider.class)
    public void calculateInstanceCount(int avgConcurrentShoppers, int peakConcurrentShoppers, int[] expectedReplicas) {
        subscriptionService.calculateInstanceCount(avgConcurrentShoppers, peakConcurrentShoppers).onItem().invoke(replicas -> {
            log.infof("Comparing expected replicas %s to actual %s for %d/%d", expectedReplicas, replicas,
                    avgConcurrentShoppers, peakConcurrentShoppers);
            assertThat(replicas, is(expectedReplicas));
        });
    }

    static class CalculatePriceTestArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("Free", 100, 0.0),
                    Arguments.of("Silver", 100, 10),
                    Arguments.of("Gold", 100, 20.0),
                    Arguments.of("Premium", 100, 30.0));
        }
    }

    static class CalculateInstanceCountTestArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(10, 100, new int[]{0, 2}),
                    Arguments.of(100, 1000, new int[]{2, 20}));
        }
    }
}