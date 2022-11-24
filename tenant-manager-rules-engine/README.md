# tenant-manager-rules-engine Project
A Quarkus project to expose SaaS services using a DMN rules engine.

## DMN rules
The following DMN rules are exposed as REST services:
* [/CostComputation](./src/main/resources/dmn/CostComputation.dmn) computes the cost associated to the selected `Tier` and `Average Concurrent Shoppers`
  * Payload example:
```json
{
  "Tier": "Free", 
  "Average Concurrent Shoppers": 10
}
```
  * Response example:
```json
{
...
  "Cost Per Hundred Units":"function Cost Per Hundred Units( Tier )",
  "Calculated Price":0.0
}
```
* [/MicroserviceByBucket](./src/main/resources/dmn/MicroserviceByBucket.dmn) returns the list of Microservice names for the given `Bucket`:
  * Payload example:
```json
{
  "Bucket": "shopper experience"
}
```
  * Response example:
```json
{
...
  "Microservice By Bucket": [
    "frontend",
    "redis-cart",
    "adservice"
  ]
}
```
* [NamespacebyTier](./src/main/resources/dmn/NamespacebyTier.dmn) returns the namespace definition for a given `Namespace`
in a given `Tier`
  * Payload example:
```json
{
  "Tier": "gold",
  "Microservice" : "frontend"
}
```
  * Response example:
```json
{
...
    "Namespace By Tier": {
        "isTenantNamespace": true,
        "namespace": ""
    }
}
```
* [](./src/main/resources/dmn/HpaComputation.dmn) computes the HPA replicas for the given `Average Concurrent Shoppers`,
`Bucket` and `Tier`.
  * Payload example:
```json
{
  "Average Concurrent Shoppers": 1200,
  "Bucket": "logistics",
  "Tier": "gold"
}
```
  * Response example:
```json
{
...
    "HPA Replicas": {
        "maxReplicas": 3,
        "minReplicas": 2,
        "replicas": 2.4,
        "comment": "1200/500"
    },
...
}
```
## /update REST endpoint
Apart from the utility DMN functions, the entry point to handle update requests is the `/update` endpoint that returns 
the HPA configuration to be configured for an aggregated list of update requests.

The request is modelled by the [ProvisioningRequest](./src/main/java/org/acme/saas/rules/model/request/ProvisioningRequest.java)
class, while the response is defined by [ProvisioningResponse](./src/main/java/org/acme/saas/rules/model/response/ProvisioningResponse.java).

* Payload example:
```json
{
    "tierRequests": [
        {
            "tier": "gold",
            "newTotalAverageConcurrentShoppers": "500",
            "tenantRequests": [
                {
                    "tenant": "Tenant1",
                    "averageConcurrentShoppers": "200"
                }
            ]
        },
        {
            "tier": "silver",
            "newTotalAverageConcurrentShoppers": "700",
            "tenantRequests": [
                {
                    "tenant": "Tenant4",
                    "averageConcurrentShoppers": "400"
                }
            ]
        }
    ]
}
```
* Response example:
```json
{
    "resourcesByTier": [
        {
            "tier": "gold",
            "resourcesByMicroservices": [
                {
                    "bucket": "enterprise utilities",
                    "tenant": null,
                    "namespace": "enterprise-utilities",
                    "microservice": "emailservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 1,
                        "replicas": 1.0,
                        "comment": "500/500"
                    }
                },
                {
                    "bucket": "enterprise utilities",
                    "tenant": null,
                    "namespace": "enterprise-utilities",
                    "microservice": "paymentservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 1,
                        "replicas": 1.0,
                        "comment": "500/500"
                    }
                },
                {
                    "bucket": "enterprise utilities",
                    "tenant": null,
                    "namespace": "enterprise-utilities",
                    "microservice": "currencyservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 1,
                        "replicas": 1.0,
                        "comment": "500/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-ops",
                    "microservice": "checkoutservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 1,
                        "replicas": 1.0,
                        "comment": "500/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-ops",
                    "microservice": "cartservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 1,
                        "replicas": 1.0,
                        "comment": "500/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-ops",
                    "microservice": "shippingservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 1,
                        "replicas": 1.0,
                        "comment": "500/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-ops",
                    "microservice": "recommendationservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 1,
                        "replicas": 1.0,
                        "comment": "500/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-ops",
                    "microservice": "productcatalogservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 1,
                        "replicas": 1.0,
                        "comment": "500/500"
                    }
                },
                {
                    "bucket": "shopper experience",
                    "tenant": "Tenant1",
                    "namespace": "Tenant1",
                    "microservice": "frontend",
                    "hpaResources": {
                        "minReplicas": 2,
                        "maxReplicas": 2,
                        "replicas": 2.0,
                        "comment": "200/100"
                    }
                },
                {
                    "bucket": "shopper experience",
                    "tenant": "Tenant1",
                    "namespace": "Tenant1",
                    "microservice": "redis-cart",
                    "hpaResources": {
                        "minReplicas": 2,
                        "maxReplicas": 2,
                        "replicas": 2.0,
                        "comment": "200/100"
                    }
                },
                {
                    "bucket": "shopper experience",
                    "tenant": "Tenant1",
                    "namespace": "Tenant1",
                    "microservice": "adservice",
                    "hpaResources": {
                        "minReplicas": 2,
                        "maxReplicas": 2,
                        "replicas": 2.0,
                        "comment": "200/100"
                    }
                }
            ]
        },
        {
            "tier": "silver",
            "resourcesByMicroservices": [
                {
                    "bucket": "enterprise utilities",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "emailservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 2,
                        "replicas": 1.4,
                        "comment": "700/500"
                    }
                },
                {
                    "bucket": "enterprise utilities",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "paymentservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 2,
                        "replicas": 1.4,
                        "comment": "700/500"
                    }
                },
                {
                    "bucket": "enterprise utilities",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "currencyservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 2,
                        "replicas": 1.4,
                        "comment": "700/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "checkoutservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 2,
                        "replicas": 1.4,
                        "comment": "700/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "cartservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 2,
                        "replicas": 1.4,
                        "comment": "700/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "shippingservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 2,
                        "replicas": 1.4,
                        "comment": "700/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "recommendationservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 2,
                        "replicas": 1.4,
                        "comment": "700/500"
                    }
                },
                {
                    "bucket": "logistics",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "productcatalogservice",
                    "hpaResources": {
                        "minReplicas": 1,
                        "maxReplicas": 2,
                        "replicas": 1.4,
                        "comment": "700/500"
                    }
                },
                {
                    "bucket": "shopper experience",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "frontend",
                    "hpaResources": {
                        "minReplicas": 6,
                        "maxReplicas": 7,
                        "replicas": 7.0,
                        "comment": "700/100"
                    }
                },
                {
                    "bucket": "shopper experience",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "redis-cart",
                    "hpaResources": {
                        "minReplicas": 6,
                        "maxReplicas": 7,
                        "replicas": 7.0,
                        "comment": "700/100"
                    }
                },
                {
                    "bucket": "shopper experience",
                    "tenant": null,
                    "namespace": "boutique-silver",
                    "microservice": "adservice",
                    "hpaResources": {
                        "minReplicas": 6,
                        "maxReplicas": 7,
                        "replicas": 7.0,
                        "comment": "700/100"
                    }
                }
            ]
        }
    ]
}
```

## Run as local Quarkus application
Prerequisites:
* Java JDK 11
* Maven 3.8
* `curl` or any other HTTP client

Just run `mvn quarkus:dev` to start the application locally. Then use one of the following `curl` commands to invoke the
DMN services:
* `/CostComputation`:
```bash
curl -X POST 'http://localhost:8080/CostComputation' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
-d '{ "Tier": "Free", "Average Concurrent Shoppers": 10 }'
```
* `/MicroserviceByBucket`:
```bash
curl -X POST 'http://localhost:8080/MicroserviceByBucket' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
-d '{ "Bucket": "shopper experience" }'
```
* `/NamespaceByTier`:
```bash
curl -X POST 'http://localhost:8080/NamespaceByTier' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
-d '{ "Tier": "gold",
    "Microservice" : "frontend" }'
```
* `/HpaComputation`:
```bash
curl -X POST 'http://localhost:8080/HpaComputation' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
-d '{
    "Average Concurrent Shoppers": 1200,
    "Bucket": "logistics",
    "Tier": "gold"
}'
```
* `/update`:
```bash
curl -X POST 'http://localhost:8080/update' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
-d '{
...
}'
```

## Deploying as a Serverless service
Prerequisites:
* Java JDK 11
* Maven 3.8
* `quarkus` CLI

Run the following to deploy the `Knative` serverless Service in the `saas-rules` namespace:
```bash
quarkus build \
-Dquarkus.kubernetes.deploy=true \
-Dquarkus.kubernetes.deployment-target=knative \
-Dquarkus.knative.cluster-local=false \
-Dquarkus.container-image.group=saas-rules \
-Dquarkus.container-image.registry=image-registry.openshift-image-registry.svc:5000 \
-Dquarkus.knative.namespace=saas-rules 
```
Notes:
* Remove the `Dquarkus.knative.cluster-local` property if you want to expose an external URL to trigger the service
* Once the service has been deployed, you can find the corresponding `knative.yaml` file under the `target\kubernetes` folder
