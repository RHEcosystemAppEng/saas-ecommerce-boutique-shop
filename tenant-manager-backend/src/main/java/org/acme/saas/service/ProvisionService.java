package org.acme.saas.service;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.acme.saas.model.data.ProvisionRequest;
import org.acme.saas.model.data.ProvisionResponse;
import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.draft.TenantDraft;
import org.acme.saas.restclient.RulesClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class ProvisionService {
    private Logger log = Logger.getLogger(ProvisionService.class);

    @RestClient
    RulesClient rulesClient;

    @ConfigProperty(name = "org.acme.saas.service.SubscriptionService.scriptFile")
    String scriptFile;
    @ConfigProperty(name = "org.acme.saas.service.SubscriptionService.purgeScriptFile")
    String purgeScriptFile;
    @ConfigProperty(name = "org.acme.saas.service.SubscriptionService.updateScriptFile")
    String updateScriptFile;

    public Uni<ProvisionResponse> provisionTier(ProvisionRequest provisionRequest) {
        return rulesClient.updateTierProvisionPlan(provisionRequest);
    }

    public Uni<String> onResourceUpdate(String tenantName, int minReplicas, int maxReplicas) {
        return runScript(updateScriptFile,
                getNamespaceName(tenantName),
                Integer.toString(minReplicas),
                Integer.toString(maxReplicas));
    }

    private String getNamespaceName(String tenantName) {
        return tenantName.replaceAll("\\s", "-");
    }

    public Uni<String> onNewSubscription(TenantDraft tenantDraft, RequestDraft requestDraft) {
        return runScript(scriptFile,
                getNamespaceName(tenantDraft.getTenantName()),
                requestDraft.getHostName(),
                requestDraft.getTier());
    }

     public Uni<String> onPurgeSubscription(TenantDraft tenantDraft, RequestDraft requestDraft) {
        return runScript(purgeScriptFile,
                getNamespaceName(tenantDraft.getTenantName()),
                requestDraft.getHostName(),
                requestDraft.getTier());
    }


    private Uni<String> runScript(String... scriptAndArgs) {
        log.infof("Calling the shell script %s with args %s", scriptAndArgs[0],
                List.of(Arrays.copyOfRange(scriptAndArgs, 1,
                        scriptAndArgs.length)));
        return Uni.createFrom()
                .item(() -> runScriptUsingBlockingIO(scriptAndArgs))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    private String runScriptUsingBlockingIO(String... scriptAndArgs) {
        String output;
        try {
            ProcessBuilder pb = new ProcessBuilder(scriptAndArgs);
            Process p = pb.start();
            InputStream is = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            String lastLine = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                lastLine = line;
            }
            log.infof("Received %s", lastLine);

            output = lastLine;
        } catch (IOException e) {
            e.printStackTrace();
            output = e.getMessage();
        }
        return output;
    }
}
