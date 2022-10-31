#!/bin/bash
TENANT_NAMESPACE=$1 ## This is the namespace where the Boutique Shop has been deployed,
MIN_REPLICAS=$2 ## This is the new minReplicas for all the HPAs
MAX_REPLICAS=$3 ## This is the new maxReplicas for all the HPAs

echo "Running $0($TENANT_NAMESPACE, $MIN_REPLICAS, $MAX_REPLICAS)"
echo "Going to sleep 4s"
sleep 4
echo "DONE"