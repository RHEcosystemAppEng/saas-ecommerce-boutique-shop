# VERSION defines the project version for the bundle.
# Update this value when you upgrade the version of your project.
# To re-generate a bundle for another specific version without changing the standard setup, you can:
# - use the VERSION as arg of the bundle target (e.g make bundle VERSION=0.0.2)
# - use environment variables to overwrite this value (e.g export VERSION=0.0.2)
VERSION ?= 0.0.1

# BOUTIQUE_SHOP_OPERATOR_NAME defines the name of the boutique shop operator
BOUTIQUE_SHOP_OPERATOR_NAME ?=boutique-shop-operator

# OPERATOR_NAME defines the name of the operator
TENANT_OPERATOR_NAME ?=saas-tenant-operator

# BOUTIQUE_SHOP_IMAGE_TAG_BASE defines the image repository for boutique shop operator
BOUTIQUE_SHOP_IMAGE_TAG_BASE ?=quay.io/ecosystem-appeng/${BOUTIQUE_SHOP_OPERATOR_NAME}

# BOUTIQUE_SHOP_IMAGE_TAG_BASE defines the image repository for boutique shop operator
TENANT_IMAGE_TAG_BASE ?=quay.io/ecosystem-appeng/${TENANT_OPERATOR_NAME}

# BOUTIQUE_SHOP_OPERATOR_NAMESPACE in which to deploy the boutique shop operator
# By default using the same name as the operator name
BOUTIQUE_SHOP_OPERATOR_NAMESPACE ?=${BOUTIQUE_SHOP_OPERATOR_NAME}

# TENANT_OPERATOR_NAMESPACE in which to deploy the boutique shop operator
# By default using the same name as the operator name
TENANT_OPERATOR_NAMESPACE ?=${TENANT_OPERATOR_NAME}

# BOUTIQUE_SHOP_OPERATOR_DIR is the directory of boutique-shop-operator
BOUTIQUE_SHOP_OPERATOR_DIR ?=boutique-shop-operator

# TENANT_OPERATOR_DIR is the directory of tenant manager operator
TENANT_OPERATOR_DIR ?=tenant-manager-operator

.PHONY: boutique-shop-bundle
boutique-shop-bundle: # Make the boutique shop operator image
	cd $(shell pwd)/${BOUTIQUE_SHOP_OPERATOR_DIR} && \
	make bundle IMAGE_TAG_BASE="${BOUTIQUE_SHOP_IMAGE_TAG_BASE}" VERSION=${VERSION}

.PHONY: tenant-bundle
tenant-bundle: # Make the tenant operator image
	cd $(shell pwd)/${TENANT_OPERATOR_DIR} && \
	make bundle IMAGE_TAG_BASE="${TENANT_IMAGE_TAG_BASE}" VERSION=${VERSION}

.PHONY: bundle
bundle: boutique-shop-bundle tenant-bundle

.PHONY: boutique-shop-deploy
boutique-shop-deploy: # Deploy boutique shop operator
	cd $(shell pwd)/${BOUTIQUE_SHOP_OPERATOR_DIR} && \
	make deploy IMAGE_TAG_BASE="${BOUTIQUE_SHOP_IMAGE_TAG_BASE}" VERSION=${VERSION}

.PHONY: tenant-deploy
tenant-deploy: # Deploy tenant operator
	cd $(shell pwd)/${TENANT_OPERATOR_DIR} && \
	make deploy IMAGE_TAG_BASE="${TENANT_IMAGE_TAG_BASE}" VERSION=${VERSION}

.PHONY: deploy
deploy: boutique-shop-deploy tenant-deploy

.PHONY: boutique-shop-bundle-deploy
boutique-shop-bundle-deploy: # Deploy boutique shop operator
	cd $(shell pwd)/${BOUTIQUE_SHOP_OPERATOR_DIR} && \
	make bundle-deploy IMAGE_TAG_BASE="${BOUTIQUE_SHOP_IMAGE_TAG_BASE}" VERSION=${VERSION}

.PHONY: tenant-bundle-deploy
tenant-bundle-deploy: # Bundle & Deploy tenant operator
	cd $(shell pwd)/${TENANT_OPERATOR_DIR} && \
	make bundle-deploy IMAGE_TAG_BASE="${TENANT_IMAGE_TAG_BASE}" VERSION=${VERSION}

.PHONY: bundle-deploy
bundle-deploy: boutique-shop-bundle-deploy tenant-bundle-deploy


.PHONY: boutique-shop-undeploy
boutique-shop-undeploy: # Undeploy boutique shop operator
	cd $(shell pwd)/${BOUTIQUE_SHOP_OPERATOR_DIR} && \
	make undeploy IMAGE_TAG_BASE="${BOUTIQUE_SHOP_IMAGE_TAG_BASE}" VERSION=${VERSION}

.PHONY: tenant-undeploy
tenant-undeploy: # Undeploy tenant operator
	cd $(shell pwd)/${TENANT_OPERATOR_DIR} && \
	make undeploy IMAGE_TAG_BASE="${TENANT_IMAGE_TAG_BASE}" VERSION=${VERSION}

.PHONY: undeploy
undeploy: boutique-shop-undeploy tenant-undeploy
 
 