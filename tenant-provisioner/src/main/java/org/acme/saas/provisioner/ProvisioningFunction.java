package org.acme.saas.provisioner;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.acme.saas.provisioner.event.NewTenantRequest;
import org.acme.saas.provisioner.event.ProvisioningEventNotifier;
import org.acme.saas.provisioner.event.ProvisioningRequestStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import io.quarkus.funqy.knative.events.CloudEventMapping;

public class ProvisioningFunction {
    private static final Logger log = Logger.getLogger(ProvisioningFunction.class);

    @ConfigProperty(name = "k-revision")
    String revisionName;

    @Inject
    ProvisioningService provisioningService;

    @Funq
    @CloudEventMapping(trigger = NewTenantRequest.EVENT_TYPE, responseSource = ProvisioningEventNotifier.EVENT_SOURCE, responseType = ProvisioningRequestStatus.EVENT_TYPE)
    @Transactional
    public ProvisioningRequestStatus provision(CloudEvent<NewTenantRequest> event) {
        NewTenantRequest newTenantRequest = event.data();
        log.infof("[%s] - Event received with type %s and data: %s\n", revisionName, event.type(),
                newTenantRequest);

        try {
            provisioningService.doProvisionAndNotifyProgress(newTenantRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.infof("[%s] - New tenant provisioned: %s\n", revisionName, newTenantRequest);
        return ProvisioningRequestStatus.builder().tenandId(newTenantRequest.getTenandId())
                .tenantName(newTenantRequest.getTenantName()).status(ProvisioningRequestStatus.Status.Completed).build();
    }
}