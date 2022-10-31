#!/bin/bash

TENANT_NAMESPACE=$1 ## This is the namespace where the Boutique Shop has been deployed,
MIN_REPLICAS=$2 ## This is the new minReplicas for all the HPAs
MAX_REPLICAS=$3 ## This is the new maxReplicas for all the HPAs

oc project ${TENANT_NAMESPACE}

for hpa in $(oc get hpa -n boutique-free -oname)
do
  echo "Patching ${hpa} in namespace ${TENANT_NAMESPACE}"
  oc patch ${hpa} -p "{\"spec\":{\"minReplicas\":${MIN_REPLICAS}, \"maxReplicas\":${MAX_REPLICAS}}}"
done