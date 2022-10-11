package org.acme.saas.provisioner;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.saas.provisioner.event.NewTenantRequest;
import org.acme.saas.provisioner.event.ProvisioningEventNotifier;
import org.acme.saas.provisioner.event.ProvisioningRequestStatus;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class ProvisioningService {
    private static final Logger log = Logger.getLogger(ProvisioningFunction.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    ProvisioningEventNotifier eventNotifier;

    @Inject
    OpenShiftProvisioner openShiftProvisioner;

    public void doProvisionAndNotifyProgress(NewTenantRequest newTenantRequest) throws InterruptedException {
        log.infof("Provisioning tenant resources for %s", newTenantRequest);

        eventNotifier.emitProvisioningStatus(newTenantRequest, ProvisioningRequestStatus.Status.Initiated);
        try {
            openShiftProvisioner.provisionNewTenant(newTenantRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        eventNotifier.emitProvisioningStatus(newTenantRequest, ProvisioningRequestStatus.Status.Completed);
    }
}
