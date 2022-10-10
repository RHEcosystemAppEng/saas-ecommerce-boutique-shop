package org.acme.saas.provisioner;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.saas.provisioner.event.NewTenantRequest;
import org.acme.saas.provisioner.event.ProvisioningRequestStatus;
import org.acme.saas.provisioner.event.ResourceProvisioningStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.jackson.JsonFormat;

@ApplicationScoped
public class ProvisioningService {
    private static final Logger log = Logger.getLogger(ProvisioningFunction.class);
    @ConfigProperty(name = "k-sink")
    String brokerUrl;

    @Inject
    ObjectMapper mapper;

    @Inject
    @RestClient
    EventNotifier eventNotifier;

    public void doProvisionAndNotifyProgress(NewTenantRequest newTenantRequest) throws InterruptedException {
        log.infof("Provisioning tenant resources for %s and notifying progress to %s", newTenantRequest, brokerUrl);

        TimeUnit.SECONDS.sleep(1L);
        emitProvisioningStatus(newTenantRequest, ProvisioningRequestStatus.Status.Initiated);

        TimeUnit.SECONDS.sleep(1L);
        emitResourceProvisioningStatus(newTenantRequest, "AAA", "Deployment", ResourceProvisioningStatus.Status.Initiated);
        TimeUnit.MILLISECONDS.sleep(500L);
        emitResourceProvisioningStatus(newTenantRequest, "AAA", "Deployment", ResourceProvisioningStatus.Status.Completed);
        TimeUnit.MILLISECONDS.sleep(500L);
        emitResourceProvisioningStatus(newTenantRequest, "BBB", "Service", ResourceProvisioningStatus.Status.Initiated);
        TimeUnit.MILLISECONDS.sleep(500L);
        emitResourceProvisioningStatus(newTenantRequest, "BBB", "Service", ResourceProvisioningStatus.Status.Completed);

        TimeUnit.MILLISECONDS.sleep(500L);
        emitProvisioningStatus(newTenantRequest, ProvisioningRequestStatus.Status.Completed);
    }

    private void emitProvisioningStatus(NewTenantRequest newTenantRequest, ProvisioningRequestStatus.Status status) {
        CloudEvent event = CloudEventBuilder.v1()
                .withSource(URI.create("provisioner"))
                .withType(ProvisioningRequestStatus.EVENT_TYPE)
                .withId(UUID.randomUUID().toString())
                .withDataContentType(JsonFormat.CONTENT_TYPE)
                .withData(createProvisioningStatus(newTenantRequest, status))
                .build();
        log.infof("Emitting provisioning request event for %s/%s, with status %s to %s", newTenantRequest.getTenantName(),
                newTenantRequest.getTenandId(), status, brokerUrl);
        eventNotifier.emit(event);
    }

    private void emitResourceProvisioningStatus(NewTenantRequest newTenantRequest, String resourceName, String resourcetype,
            ResourceProvisioningStatus.Status status) {
        CloudEvent event = CloudEventBuilder.v1()
                .withSource(URI.create("provisioner"))
                .withType(ResourceProvisioningStatus.EVENT_TYPE)
                .withId(UUID.randomUUID().toString())
                .withDataContentType(JsonFormat.CONTENT_TYPE)
                .withData(createResourceProvisioningStatus(newTenantRequest, resourceName, resourcetype, status))
                .build();
        log.infof("Emitting resouce provisioning event for %s/%s, with status %s to %s", resourceName, resourcetype, status,
                brokerUrl);
        eventNotifier.emit(event);
    }

    private CloudEventData createProvisioningStatus(NewTenantRequest newTenantRequest,
            ProvisioningRequestStatus.Status status) {
        ProvisioningRequestStatus provisioningRequestStatus = ProvisioningRequestStatus.builder()
                .tenandId(newTenantRequest.getTenandId())
                .tenantName(newTenantRequest.getTenantName()).status(status).build();
        return PojoCloudEventData.wrap(provisioningRequestStatus, mapper::writeValueAsBytes);
    }

    private CloudEventData createResourceProvisioningStatus(NewTenantRequest newTenantRequest,
            String resourceName, String resourcetype, ResourceProvisioningStatus.Status status) {
        ResourceProvisioningStatus resourceProvisioningStatus = ResourceProvisioningStatus.builder()
                .tenandId(newTenantRequest.getTenandId())
                .resourceName(resourceName).resourceType(resourcetype).status(status).build();
        return PojoCloudEventData.wrap(resourceProvisioningStatus, mapper::writeValueAsBytes);
    }
}
