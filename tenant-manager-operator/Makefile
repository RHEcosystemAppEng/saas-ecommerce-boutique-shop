# VERSION defines the project version for the bundle.
# Update this value when you upgrade the version of your project.
# To re-generate a bundle for another specific version without changing the standard setup, you can:
# - use the VERSION as arg of the bundle target (e.g make bundle VERSION=0.0.2)
# - use environment variables to overwrite this value (e.g export VERSION=0.0.2)
VERSION ?= 0.0.2

# HELM_REPO defines the helm repo for the helm chart
HELM_REPO=../

# HELM_CHART defines the tenant manager chart for the operator
HELM_CHART=tenant-manager-chart

# DOMAIN defines the domain used to initialize the operator
DOMAIN ?=ecosystem-appeng.redhat.com

# IMAGE_TAG_BASE defines the image repository for operator
IMAGE_TAG_BASE=quay.io/ecosystem-appeng/saas-tenant-operator

# IMG defines the tagged image with the version information
IMG=${IMAGE_TAG_BASE}:v${VERSION}

# BUNDLE_IMG defines the name and version of the bundle image
BUNDLE_IMG=${IMAGE_TAG_BASE}-bundle:v${VERSION}

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
	mkdir -p $(shell pwd)/${OPERATOR_DIR} 

.PHONY: init
init: setup
	cd $(shell pwd)/${OPERATOR_DIR} && \
	operator-sdk init --verbose --plugins=helm.sdk.operatorframework.io/v1 \
	--domain=${DOMAIN} \
	--helm-chart=$(shell pwd)/../${HELM_CHART} \
	--kind=TenantManager
	$(shell pwd)/scripts/post-init.sh "$(shell pwd)/${OPERATOR_DIR}" "${OPERATOR_NAME}" "${NAMESPACE}"


.PHONY: bundle
bundle:
	# create manifest file
	$(shell pwd)/scripts/manifest.sh "$(shell pwd)/${OPERATOR_DIR}" "${OPERATOR_NAME}"

	# Make the operator image
	cd $(shell pwd)/${OPERATOR_DIR} && \
	make docker-build docker-push IMG="${IMG}" VERSION=${VERSION}

	# Make the operator bundle by adding OLM to the operator image
	cd $(shell pwd)/${OPERATOR_DIR} && \
	make bundle IMG=${IMG} IMAGE_TAG_BASE=${IMAGE_TAG_BASE} VERSION=${VERSION}

	cd $(shell pwd)/${OPERATOR_DIR} && \
	make bundle-build bundle-push IMG="${IMG}" IMAGE_TAG_BASE="${IMAGE_TAG_BASE}" VERSION=${VERSION}

.PHONY: deploy
deploy:
	# Make a new namespace and deploy the operator
	oc new-project ${NAMESPACE} || true
	operator-sdk run bundle ${BUNDLE_IMG}

.PHONY: build-deploy
build-deploy: init bundle deploy

.PHONY: all
all: build-deploy

.PHONY: undeploy
undeploy:
	oc delete ns boutique-ops enterprise-utilities boutique-free boutique-silver || true
	cd $(shell pwd)/${OPERATOR_DIR} && \
	make undeploy || true
	oc delete --all TenantManager --namespace=${NAMESPACE} || true
	oc delete ns saas-boutique || true
	oc delete ns ${NAMESPACE}  || true
 