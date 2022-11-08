# VERSION defines the project version for the bundle.
# Update this value when you upgrade the version of your project.
# To re-generate a bundle for another specific version without changing the standard setup, you can:
# - use the VERSION as arg of the bundle target (e.g make bundle VERSION=0.0.2)
# - use environment variables to overwrite this value (e.g export VERSION=0.0.2)
VERSION ?= 0.0.1

# HELM_REPO defines the helm repo for the helm chart
HELM_REPO=https://rhecosystemappeng.github.io/saas-ecommerce-boutique-shop

# HELM_CHART defines the tenant manager chart for the operator
HELM_CHART=tenant-manager-chart

# DOMAIN defines the domain used to initialize the operator
DOMAIN ?=ecosystem-appeng.redhat.com

# IMAGE_TAG_BASE defines the image repository for operator
IMAGE_TAG_BASE="quay.io/ecosystem-appeng/saas-tenant-operator"

# IMG defines the tagged image with the version information
IMG="${IMAGE_TAG_BASE}:v${VERSION}"

# BUNDLE_IMG defines the name and version of the bundle image
BUNDLE_IMG="${IMAGE_TAG_BASE}-bundle:v${VERSION}"

# OPERATOR_NAME defines the name of the operator
OPERATOR_NAME ?=boutique-tenant-operator

# NAMESPACE in which to deploy the boutique tenant operator
# By default using the same name as the operator name
NAMESPACE ?=${OPERATOR_NAME}

# OPERATOR_DIR defines the directory in which the operator code is generator
OPERATOR_DIR=boutique-tenant-operator

.PHONY: setup
setup:
	rm -rf $(shell pwd)/${OPERATOR_DIR} && \
	mkdir -p $(shell pwd)/${OPERATOR_DIR} && \
	helm repo add rh-saas ${HELM_REPO}  > /dev/null 2>&1
	helm repo update

.PHONY: init
init: setup
	cd $(shell pwd)/${OPERATOR_DIR} && \
	operator-sdk init --verbose --plugins=helm.sdk.operatorframework.io/v1 \
	--domain=${DOMAIN} \
	--helm-chart=${HELM_CHART} \
	--helm-chart-repo=${HELM_REPO} \
	--kind=TenantManager
	$(shell pwd)/post-init.sh "$(shell pwd)/${OPERATOR_DIR}" "${OPERATOR_NAME}" "${NAMESPACE}"


