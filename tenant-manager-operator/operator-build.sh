#!/bin/bash

set -e
set -o pipefail

HELM_REPO=https://rhecosystemappeng.github.io/saas-ecommerce-boutique-shop
HELM_CHART=rh-saas/tenant-manager-chart
CLUSTER_DOMAIN=$(oc get ingresses.config.openshift.io cluster -ojsonpath='{.spec.domain}')

# Returns 0 if the OLM status is missing, 1 otherwise
function checkOlmStatus() {
  log "Checking OLM status"
  systemControllerOperator=$(operator-sdk olm status)
  if [ ! -z "$systemControllerOperator" ]; then
    log "The OLM is not responding. Please validate that you are connected to and OpenShift cluster."
    return 1
  else
    return 0
  fi
}

# Connecto the helm repo and install the chart
#helm repo add rh-saas $HELM_REPO
#helm install demo-release $HELM_CHART  #May not need to call this line since it is called later in the script

# Stage the operator
operator-sdk init --verbose --plugins=helm.sdk.operatorframework.io/v1 \
  --domain=$CLUSTER_DOMAIN \
  --project-name=boutique-demo \
  --helm-chart=$HELM_CHART \
  --helm-chart-repo=$HELM_REPO

# Update the operator CRDs and OLM to the latest version
oc apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.22.0/crds.yaml
oc apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.22.0/olm.yaml

# Validate OLM status
checkOlmStatus

# Make the operator image
make docker-build docker-push IMG="quay.io/ecosystem-appeng/saas-tenant-operator:v0.0.1"

# Make the operator bundle by adding OLM to the operator image
make bundle IMG="quay.io/ecosystem-appeng/saas-tenant-operator:v0.0.1"
make bundle-build bundle-push IMG="quay.io/ecosystem-appeng/saas-tenant-operator:v0.0.1"

# Make a new namespace and deploy the operator
oc new-project boutique-operator-demo
operator-sdk run bundle quay.io/ecosystem-appeng/saas-tenant-operator:v0.0.1

