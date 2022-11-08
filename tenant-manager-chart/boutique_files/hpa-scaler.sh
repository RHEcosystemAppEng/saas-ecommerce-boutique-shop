#!/bin/bash

TENANT_NAMESPACE=$(echo "$1" | tr '[:upper:]' '[:lower:]')  ## This is the namespace where the Boutique Shop has been deployed
MIN_REPLICAS=$(echo "$2" | tr '[:upper:]' '[:lower:]')  ## This is the new minReplicas for all the HPAs
MAX_REPLICAS=$(echo "$3" | tr '[:upper:]' '[:lower:]')  ## This is the new maxReplicas for all the HPAs

oc project ${TENANT_NAMESPACE}

for hpa in $(oc get hpa -n ${TENANT_NAMESPACE} -oname)
do
  echo "Patching ${hpa} in namespace ${TENANT_NAMESPACE}"
  oc patch ${hpa} -p "{\"spec\":{\"minReplicas\":${MIN_REPLICAS}, \"maxReplicas\":${MAX_REPLICAS}}}"
done
