package com.saas.tenant.manager.service;

import com.saas.tenant.manager.model.Subscription;
import com.saas.tenant.manager.model.Tenant;
import com.saas.tenant.manager.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription save(Tenant tenant,
                             String serviceLevel,
                             String avgConcurrentShoppers,
                             String peakConcurrentShoppers,
                             String fromTime, String toTime) {

        Subscription subscription = new Subscription();
        subscription.setTenantName(tenant.getTenantUserName());
        subscription.setTenant(tenant);
        subscription.setServiceLevel(serviceLevel);
        subscription.setAvgConcurrentShoppers(avgConcurrentShoppers);
        subscription.setPeakConcurrentShoppers(peakConcurrentShoppers);
        subscription.setFromTime(fromTime);
        subscription.setToTime(toTime);
        subscription.setStatus("Pending");
        subscription.setCreatedDate(Instant.now());

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        String namespaceName = savedSubscription.getTenantName().replaceAll("\\s", "-") +
                System.currentTimeMillis();

        try {
            ProcessBuilder pb = new ProcessBuilder("./src/main/resources/ocp/create-namespace.sh", namespaceName);
            Process p = pb.start();
            InputStream is = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = null;
            String lastLine = "";
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
            if (lastLine.contains("http:")) {
                String routeUrl = lastLine.substring(lastLine.indexOf("http:"));
                System.out.println("URL --->" + routeUrl);
                savedSubscription.setUrl(routeUrl);
                savedSubscription.setStatus("Active");
            } else {
                System.out.println("URL couldn't fetch from the FreshRSS crd!!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Subscription save = subscriptionRepository.save(savedSubscription);
        return save;
    }
}
