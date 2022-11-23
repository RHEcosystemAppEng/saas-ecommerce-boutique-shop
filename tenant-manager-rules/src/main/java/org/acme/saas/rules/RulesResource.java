package org.acme.saas.rules;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.saas.rules.model.request.ProvisioningRequest;
import org.acme.saas.rules.model.request.TenantUpdateRequest;
import org.acme.saas.rules.model.request.TierUpdateRequest;
import org.acme.saas.rules.model.response.HpaResources;
import org.acme.saas.rules.model.response.ProvisioningResponse;
import org.acme.saas.rules.model.response.ResourcesByMicroservice;
import org.acme.saas.rules.model.response.ResourcesByTier;
import org.jboss.logging.Logger;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.kogito.Application;
import org.kie.kogito.dmn.rest.DMNJSONUtils;
import org.kie.kogito.dmn.rest.KogitoDMNResult;

@Path("/")
public class RulesResource {
    private Logger logger = Logger.getLogger(RulesResource.class);

    @javax.inject.Inject()
    Application application;

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ProvisioningResponse compute(ProvisioningRequest request) {
        logger.infof("Request is %s", request);

        ProvisioningResponse response = new ProvisioningResponse();

        for (TierUpdateRequest tierRequest : request.getTierRequests()) {
            String tier = tierRequest.getTier();
            ResourcesByTier resourcesByTier = new ResourcesByTier();
            response.getResourcesByTier().add(resourcesByTier);
            resourcesByTier.setTier(tier);

            for (String bucket : List.of("enterprise utilities", "logistics", "shopper experience")) {
                List<String> microservices = allMicroservicesByBucket(bucket);
                logger.infof("ms for %s are : %s", bucket, microservices);

                for (String microservice : microservices) {
                    String namespace = namespaceByMicroservice(tier, microservice);
                    logger.infof("namespace for %s is : %s", microservice, namespace);

                    if (namespace == null) {
                        for (TenantUpdateRequest tenantUpdateRequest : tierRequest.getTenantRequests()) {
                            // Tenant NS

                            ResourcesByMicroservice resourcesByMicroservice = new ResourcesByMicroservice();
                            resourcesByTier.getResourcesByMicroservices().add(resourcesByMicroservice);
                            resourcesByMicroservice.setBucket(bucket);
                            String tenant = tenantUpdateRequest.getTenant();
                            resourcesByMicroservice.setTenant(tenant);
                            resourcesByMicroservice.setMicroservice(microservice);
                            namespace = calculateNamespaceFromTenant(tenant);
                            resourcesByMicroservice.setNamespace(namespace);
                            resourcesByMicroservice.setHpaResources(
                                    computeHpaResources(tenantUpdateRequest.getAverageConcurrentShoppers(), bucket,
                                            tier));
                        }
                    } else {
                        ResourcesByMicroservice resourcesByMicroservice = new ResourcesByMicroservice();
                        resourcesByTier.getResourcesByMicroservices().add(resourcesByMicroservice);
                        resourcesByMicroservice.setBucket(bucket);
                        resourcesByMicroservice.setMicroservice(microservice);
                        resourcesByMicroservice.setNamespace(namespace);
                        resourcesByMicroservice.setHpaResources(
                                computeHpaResources(tierRequest.getNewTotalAverageConcurrentShoppers(), bucket, tier));
                    }
                }
            }

        }

        return response;
    }

    private List<String> allMicroservicesByBucket(String bucket) {
        org.kie.kogito.decision.DecisionModel dmn = application.get(org.kie.kogito.decision.DecisionModels.class)
                .getDecisionModel("https://kiegroup.org/dmn/ms-by-bucket", "MicroserviceByBucket");
        java.util.Map<String, Object> variables = new HashMap<>();
        variables.put("Bucket", bucket);

        org.kie.dmn.api.core.DMNResult decisionResult = dmn.evaluateAll(DMNJSONUtils.ctx(dmn, variables));
        KogitoDMNResult dmnResult = new KogitoDMNResult("https://kiegroup.org/dmn/ms-by-bucket",
                "MicroserviceByBucket", decisionResult);

        return (List<String>) dmnResult.getDecisionResults().get(0).getResult();
    }

    private String namespaceByMicroservice(String tier, String microservice) {
        org.kie.kogito.decision.DecisionModel dmn = application.get(org.kie.kogito.decision.DecisionModels.class)
                .getDecisionModel("https://kiegroup.org/dmn/ns-by-tier", "NamespaceByTier");

        java.util.Map<String, Object> variables = new HashMap<>();
        logger.infof("Invoking NamespaceByTier");
        variables.put("Tier", tier);
        variables.put("Microservice", microservice);
        org.kie.dmn.api.core.DMNResult decisionResult = dmn.evaluateAll(DMNJSONUtils.ctx(dmn, variables));
        KogitoDMNResult dmnResult = new KogitoDMNResult(
                "https://kiegroup.org/dmn/ns-by-tier",
                "NamespaceByTier", decisionResult);

        DMNDecisionResult decision = dmnResult.getDecisionResults().get(0);
        Map<String, Object> result = (Map<String, Object>) decision.getResult();
        boolean isTenantNamespace = (boolean) result.get("isTenantNamespace");
        if (!isTenantNamespace) {
            return (String) result.get("namespace");
        }
        return null;
    }

    private HpaResources computeHpaResources(long averageConcurrentShoppers, String bucket, String tier) {
        org.kie.kogito.decision.DecisionModel dmn = application.get(org.kie.kogito.decision.DecisionModels.class)
                .getDecisionModel("https://kiegroup.org/dmn/hpa-computation", "HpaComputation");

        java.util.Map<String, Object> variables = new HashMap<>();
        logger.infof("Invoking HpaComputation");
        variables.put("Average Concurrent Shoppers", averageConcurrentShoppers);
        variables.put("Bucket", bucket);
        variables.put("Tier", tier);
        logger.infof("Invoking %s with %s", "HpaComputation", variables);
        org.kie.dmn.api.core.DMNResult decisionResult = dmn.evaluateAll(DMNJSONUtils.ctx(dmn, variables));
        KogitoDMNResult dmnResult = new KogitoDMNResult(
                "https://kiegroup.org/dmn/hpa-computation",
                "HpaComputation", decisionResult);

        DMNDecisionResult decision = dmnResult.getDecisionResultByName("HPA Replicas");
        logger.infof("Result is %s", decision.getResult());
        Map<String, Object> result = (Map<String, Object>) decision.getResult();
        HpaResources hpaResources = new HpaResources(((BigDecimal) result.get("minReplicas")).intValue(),
                ((BigDecimal) result.get("maxReplicas")).intValue(),
                ((BigDecimal) result.get("replicas")).doubleValue(), (String) result.get("comment"));
        logger.infof("computeHpaResources for %d is : %s", averageConcurrentShoppers, hpaResources);
        return hpaResources;
    }

    private String calculateNamespaceFromTenant(String tenant) {
        return tenant.replaceAll("\\s", "-");
    }
}
