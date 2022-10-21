#!/bin/bash
TENANT_NAME=$1 ## This can be the namespace where the Boutique Shop will be deployed, and the name of the associated Route
TENANT_HOSTNAME=$2 ## This is the host name for the tenant access Route
TIER=$(echo "$3" | tr '[:upper:]' '[:lower:]') ## Tier selection - Ensure it's all lowercase

echo "Running $0($TENANT_NAME, $TENANT_HOSTNAME, $TIER)"
echo "TESTURL"