package org.acme.saas.service;

import org.acme.saas.model.draft.RequestDraft;
import org.acme.saas.model.draft.TenantDraft;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@ApplicationScoped
public class ProvisionService {
    private Logger log = Logger.getLogger(ProvisionService.class);

    @ConfigProperty(name = "org.acme.saas.service.SubscriptionService.scriptFile")
    String scriptFile;

    public String onNewSubscription(TenantDraft tenantDraft, RequestDraft requestDraft) {
        String namespaceName = tenantDraft.getTenantName().replaceAll("\\s", "-");

        log.infof("Calling the shell script %s", scriptFile);
        try {
            ProcessBuilder pb = new ProcessBuilder(scriptFile,
                    namespaceName,
                    requestDraft.getHostName(),
                    requestDraft.getTier());
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

            return lastLine;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
