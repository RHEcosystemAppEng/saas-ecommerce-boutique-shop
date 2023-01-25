#!/bin/bash

TENANT_NAME=$(echo "$1" | tr '[:upper:]' '[:lower:]')  ## This can be the namespace where the Boutique Shop will be deployed, and the name of the associated Route
TENANT_HOSTNAME=$(echo "$2" | tr '[:upper:]' '[:lower:]')  ## This is the host name for the tenant access Route
TIER=$(echo "$3" | tr '[:upper:]' '[:lower:]') ## Tier selection - Ensure it's all lowercase

SCRIPT_DIR=$(dirname $0)
TENANT_NAMESPACE=${TENANT_NAME}

oc delete BouiqueShop $TENANT_NAME

if [[ "${TIER}" == "free" ] || "${TIER}" == "silver" ]]; then
    oc delete ns $TENANT_NAMESPACE
fi