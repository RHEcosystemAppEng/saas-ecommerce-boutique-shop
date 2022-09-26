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
                             String serviceLevel) {

        Subscription subscription = new Subscription();
        subscription.setTenantName(tenant.getTenantUserName());
        subscription.setTenant(tenant);
        subscription.setServiceLevel(serviceLevel);
        subscription.setStatus("Pending");
        subscription.setCreatedDate(Instant.now());

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        String namespaceName = savedSubscription.getTenantName().replaceAll("\\s", "-") +
                System.currentTimeMillis();
        String directoryPath = "/boutique_files";

        try {
            ProcessBuilder pb = new ProcessBuilder( directoryPath+"/create-namespace.sh", namespaceName, directoryPath);
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
