# tenant-manager-costcomputation-rule Project
A Quarkus project to expose SaaS DMN rules as REST services:
* [/CostComputation](./src/main/resources/CostComputation.dmn) computes the cost associated to the selected `Tier` and `Average Concurrent Shoppers`
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
  "Average Concurrent Shoppers":10,
  "Tier":"Free",
  "Cost Per Hundred Units":"function Cost Per Hundred Units( Tier )",
  "Calculated Price":0.0
}
```
* [/ProvisionPlan](./src/main/resources/ProvisionPlan.dmn) computes the provisioning plan given the `Average Concurrent Shoppers`
and `Peak Concurrent Shoppers`:
    * Payload example:
```json
{
  "Average Concurrent Shoppers": 50, 
  "Peak Concurrent Shoppers": 200
}
```
* Response example:
```json
{
  "Average Concurrent Shoppers":50,
  "Compute Replicas":{
    "maxReplicas":4,
    "minReplicas":1},
  "Min Replicas":"function Min Replicas( Average Concurrent Shoppers )",
  "Peak Concurrent Shoppers":200,
  "Max Replicas":"function Max Replicas( Peak Concurrent Shoppers )"
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
* `/ProvisionPlan`:
```bash
curl -X POST 'http://localhost:8080/ProvisionPlan' \
-H 'Accept: application/json' \
-H 'Content-Type: application/json' \
-d '{ "Average Concurrent Shoppers": 50, "Peak Concurrent Shoppers": 200 }'
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
-Dquarkus.knative.cluster-local=true \
-Dquarkus.container-image.group=saas-rules \
-Dquarkus.container-image.registry=image-registry.openshift-image-registry.svc:5000 \
-Dquarkus.knative.namespace=saas-rules
```
Notes:
* Remove the `Dquarkus.knative.cluster-local` property if you want to expose an external URL to trigger the service
* Once the service has been deployed, you can find the corresponding `knative.yaml` file under the `target\kubernetes` folder
