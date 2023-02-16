#!/bin/bash

SCRIPT_DIR=$(dirname $0)
TIER=$(echo "$1" | tr '[:upper:]' '[:lower:]') ## Tier selection - Ensure it's all lowercase
RULES_RESPONSE_PAYLOAD=$(echo "$2" | tr '[:upper:]' '[:lower:]')  ## This is the rules engine response json payload specific to a given tier

echo "Applying HPA scaler CR for the Tier: $1"
oc process -f ${SCRIPT_DIR}/boutique-hpa-scaler.yaml --param=TIER=$1 --param=RULES_RESPONSE_PAYLOAD="$2" | oc apply -f -