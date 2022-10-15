package org.acme.saas.service;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.acme.saas.model.Subscription;
import org.acme.saas.model.data.SubscriptionSummaryData;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.draft.SubscriptionDraft;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.model.mappers.SubscriptionMapper;
import org.acme.saas.repository.SubscriptionRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@ApplicationScoped
public class SubscriptionService {
    private Logger log = Logger.getLogger(SubscriptionService.class);

    @ConfigProperty(name = "org.acme.saas.service.SubscriptionService.filesLocation")
    String filesLocation;
    @ConfigProperty(name = "org.acme.saas.service.SubscriptionService.scriptFile")
    String scriptFile;

    @Inject
    RequestService requestService;

    @Inject
    SubscriptionRepository subscriptionRepository;

    @ReactiveTransactional
    public Uni<Subscription> findFirstByTenantKey(String tenantKey) {
        return subscriptionRepository.findFirstByTenantKey(tenantKey);
    }

    @ReactiveTransactional
    public Uni<List<Subscription>> findAllByTenantKey(String tenantKey) {
        return subscriptionRepository.findAllByTenantKey(tenantKey);
    }

    public double calculatePrice(String tier, int avgConcurrentShoppers) {
        return switch (tier) {
            case "Silver" -> (avgConcurrentShoppers / 100.0) * 10;
            case "Gold" -> (avgConcurrentShoppers / 100.0) * 20;
            default -> 0.0;
        };
    }

    public int[] calculateInstanceCount(int avgConcurrentShoppers) {
        int min = avgConcurrentShoppers / 50;
        int max = avgConcurrentShoppers / 50;

        return new int[]{min, max};
    }

    @ReactiveTransactional
    public Uni<Subscription> createNewSubscription(TenantDraft tenantDraft, SubscriptionDraft subscriptionDraft, RequestDraft requestDraft) {

        return requestService.createNewRequest(requestDraft)
                .onItem().ifNotNull().transformToUni(request -> {
                    Subscription subscription = SubscriptionMapper.INSTANCE
                            .subscriptionDraftToSubscription(subscriptionDraft);
                    subscription.setRequest(request);

                    String namespaceName = tenantDraft.getTenantName().replaceAll("\\s", "-");

                    log.infof("Calling the shell script %s", scriptFile);
                    try {
                        ProcessBuilder pb = new ProcessBuilder(scriptFile,
                                namespaceName,
                                filesLocation,
                                requestDraft.getHostName());
                        Process p = pb.start();
                        InputStream is = p.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                        String line = null;
                        String lastLine = "";
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                            lastLine = line;
                        }
//                        if (lastLine.contains("http:")) {
//                            String routeUrl = lastLine.substring(lastLine.indexOf("http:")).replaceAll("\\s", "");
//                            System.out.println("URL --->" + routeUrl);
//                            subscription.setUrl(routeUrl);
//                            subscription.setStatus(Constants.SUBSCRIPTION_STATUS_ACTIVE);
//                        } else {
//                            System.out.println("URL couldn't fetch from the create-namespace.sh output!!!");
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return subscriptionRepository.persist(subscription);
                })
                .onItem().ifNull().failWith(InternalServerErrorException::new);
    }


    public Uni<List<SubscriptionSummaryData>> getSubscriptionSummary() {
        return subscriptionRepository.getSubscriptionSummary();
    }

}
