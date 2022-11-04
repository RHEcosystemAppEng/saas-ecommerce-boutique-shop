#!/bin/bash

set -e
set -o pipefail

rm -rf bin bundle config helm-charts bundle.Dockerfile Dockerfile Makefile PROJECT watches.yaml
oc delete ns boutique-operator-demo || true

HELM_REPO=https://rhecosystemappeng.github.io/saas-ecommerce-boutique-shop
HELM_CHART=tenant-manager-chart
CLUSTER_DOMAIN=apps.ocp.pebcac.org
IMAGE_TAG_BASE="quay.io/ecosystem-appeng/saas-tenant-operator"
VERSION=v0.0.1
IMG="$IMAGE_TAG_BASE:$VERSION"
BUNDLE_IMG="$IMAGE_TAG_BASE-bundle:$VERSION"

# Returns 0 if the OLM status is missing, 1 otherwise
#function checkOlmStatus() {
#  systemControllerOperator=$(operator-sdk olm status)
#  if [ ! -z "$systemControllerOperator" ]; then
#    return 1
#  else
#    return 0
#  fi
#}

# Stage the operator
operator-sdk init --verbose --plugins=helm.sdk.operatorframework.io/v1 \
  --domain=$CLUSTER_DOMAIN \
  --helm-chart=$HELM_CHART \
  --helm-chart-repo=$HELM_REPO

# Update the operator CRDs and OLM to the latest version
#oc apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.22.0/crds.yaml
#oc apply -f https://github.com/operator-framework/operator-lifecycle-manager/releases/download/v0.22.0/olm.yaml

# Validate OLM status
#checkOlmStatus

# Make the operator image
make docker-build docker-push IMG="$IMG"

# Make the operator bundle by adding OLM to the operator image
make bundle IMG="$IMG" IMAGE_TAG_BASE="$IMAGE_TAG_BASE"
make bundle-build bundle-push IMG="$IMG" IMAGE_TAG_BASE="$IMAGE_TAG_BASE"

# Make a new namespace and deploy the operator
oc new-project boutique-operator-demo
operator-sdk run bundle $BUNDLE_IMG

