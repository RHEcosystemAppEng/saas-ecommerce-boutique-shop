#!/bin/bash

TENANT_NAME=$(echo "$1" | tr '[:upper:]' '[:lower:]')  ## This can be the namespace where the Boutique Shop will be deployed, and the name of the associated Route
TENANT_HOSTNAME=$(echo "$2" | tr '[:upper:]' '[:lower:]')  ## This is the host name for the tenant access Route
TIER=$(echo "$3" | tr '[:upper:]' '[:lower:]') ## Tier selection - Ensure it's all lowercase

SCRIPT_DIR=$(dirname $0)

if [[ ${TIER} == "free" ]]; then
  TENANT_NAMESPACE="boutique-free"
elif [[ ${TIER} == "silver" ]]; then
  TENANT_NAMESPACE="boutique-silver"
elif [[ ${TIER} == "gold" ]]; then
  TENANT_NAMESPACE=${TENANT_NAME}
elif [[ ${TIER} == "platinum" ]]; then
  TENANT_NAMESPACE=${TENANT_NAME}
else
  echo "Unmanaged tier ${TIER}-Exiting with error code ${ERR_UNMANAGED_TIER}"
  exit ${ERR_UNMANAGED_TIER}
fi

echo "Deleting boutique shop $TENANT_NAME in namespace $TENANT_NAMESPACE"
oc delete BoutiqueShop $TENANT_NAME -n $TENANT_NAMESPACE


if [ "$TIER" = "gold" ] || [ "$TIER" = "platinum" ]; then
    echo "Deleting namespace  $TENANT_NAMESPACE"
    oc delete ns $TENANT_NAMESPACE
fi