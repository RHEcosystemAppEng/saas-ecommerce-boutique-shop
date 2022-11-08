#!/bin/bash

set -e
set -o pipefail

rm -rf bin bundle config helm-charts bundle.Dockerfile Dockerfile Makefile PROJECT watches.yaml

HELM_REPO=https://rhecosystemappeng.github.io/saas-ecommerce-boutique-shop
HELM_CHART=tenant-manager-chart
CLUSTER_DOMAIN=saas.redhat.com
IMAGE_TAG_BASE="quay.io/ecosystem-appeng/saas-tenant-operator"
INT_VERSION=0.0.31
VERSION=v$INT_VERSION
IMG="$IMAGE_TAG_BASE:$VERSION"
BUNDLE_IMG="$IMAGE_TAG_BASE-bundle:$VERSION"
NAMESPACE=boutique-tenant-operator
OPERATOR_NAME=boutique-tenant-operator

oc delete ns $NAMESPACE > /dev/null 2>&1
oc delete ns saas-boutique > /dev/null 2>&1

# Stage the operator
operator-sdk init --verbose --plugins=helm.sdk.operatorframework.io/v1 \
  --domain=$CLUSTER_DOMAIN \
  --helm-chart=$HELM_CHART \
  --helm-chart-repo=$HELM_REPO \
  --kind=TenantManager


# Make the operator image
make docker-build docker-push IMG="$IMG" VERSION=$INT_VERSION

# Make the operator bundle by adding OLM to the operator image
make bundle IMG="$IMG" IMAGE_TAG_BASE="$IMAGE_TAG_BASE" VERSION=$INT_VERSION
make bundle-build bundle-push IMG="$IMG" IMAGE_TAG_BASE="$IMAGE_TAG_BASE" VERSION=$INT_VERSION

# Make a new namespace and deploy the operator
oc new-project $NAMESPACE
operator-sdk run bundle $BUNDLE_IMG


