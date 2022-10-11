package org.acme.saas.provisioner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.acme.saas.provisioner.event.NewTenantRequest;
import org.acme.saas.provisioner.event.ProvisioningEventNotifier;
import org.acme.saas.provisioner.event.ResourceProvisioningStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import freemarker.core.PlainTextOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.VersionInfo;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.fabric8.kubernetes.client.dsl.ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.fabric8.openshift.client.OpenShiftClient;

@ApplicationScoped
public class OpenShiftProvisioner {
    private static final Logger log = Logger.getLogger(OpenShiftProvisioner.class);

    @Inject
    ProvisioningEventNotifier eventNotifier;
    @Inject
    Configuration templateConfiguration;
    @Inject
    private OpenShiftClient openshiftClient;
    @ConfigProperty(name = "provisioner.templates.folder")
    private String templatesFolder;
    @ConfigProperty(name = "provisioner.templates.files")
    private String templateFiles;

    public void provisionNewTenant(NewTenantRequest newTenantRequest)
            throws IOException, InterruptedException, TemplateException {
        VersionInfo versionInfo = openshiftClient.getVersion();
        log.infof("Connected to OpenShift platform %s at %s: %s", versionInfo.getPlatform(), openshiftClient.getOpenshiftUrl(),
                versionInfo.getMajor() + "." + versionInfo.getMinor());

        String namespaceName = newTenantRequest.getTenantName().toLowerCase();
        Namespace namespace = openshiftClient.namespaces().withName(namespaceName).get();
        if (namespace == null) {
            log.infof("No namespace %s found, creating it", namespaceName);
            namespace = openshiftClient.namespaces().create(new NamespaceBuilder()
                    .withNewMetadata()
                    .withName(namespaceName)
                    .addToLabels("owner", OpenShiftProvisioner.class.getCanonicalName())
                    .endMetadata()
                    .build());
            log.infof("Namespace %s created", namespaceName);
        } else {
            log.infof("Found existing namespace %s", namespaceName);
        }

        openshiftClient.apps().deployments().watch(new Watcher<Deployment>() {
            @Override
            public void eventReceived(Action action, Deployment resource) {
                log.infof("Received Deployment event: %s", resource);
            }

            @Override
            public void onClose(WatcherException cause) {
                log.infof("Closing watcher");
            }
        });

        log.infof("Deploying folder %s", templatesFolder);
        templateConfiguration.setOutputFormat(PlainTextOutputFormat.INSTANCE);

        for (String template : templateFiles.split(",")) {
            log.infof("Deploying template %s", template);
            File templateFile = Path.of(templatesFolder, template).toFile();
            if (!templateFile.exists()) {
                log.warnf("Skipping missing file %s", template);
            } else if (templateFile.isDirectory()) {
                templateConfiguration.setDirectoryForTemplateLoading(Paths.get(templatesFolder, template).toFile());
                for (String child : templateFile.list()) {
                    deployTemplate(newTenantRequest, child);
                }
            } else {
                templateConfiguration.setDirectoryForTemplateLoading(new File(templatesFolder));
                deployTemplate(newTenantRequest, template);
            }
        }
    }

    private void deployTemplate(NewTenantRequest newTenantRequest, String templateName) throws IOException, TemplateException {
        eventNotifier.emitResourceProvisioningStatus(newTenantRequest, templateName, "YAML",
                ResourceProvisioningStatus.Status.Initiated);

        Template template = templateConfiguration.getTemplate(templateName);

        Map<String, Object> input = new HashMap<String, Object>();
        String namespaceName = newTenantRequest.getTenantName().toLowerCase();
        input.put("namespace", namespaceName);
        StringWriter writer = new StringWriter();
        template.process(input, writer);
        ParameterNamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> all = openshiftClient
                .load(new ByteArrayInputStream(writer.toString().getBytes()));
        log.infof("Loaded %s", templateName);
        log.debugf("Template file %s rendered as:\n%s", templateName, template);

        all.inNamespace(namespaceName).createOrReplace();
        eventNotifier.emitResourceProvisioningStatus(newTenantRequest, templateName, "YAML",
                ResourceProvisioningStatus.Status.Completed);
    }
}
