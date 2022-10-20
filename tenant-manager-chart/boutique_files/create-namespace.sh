#!/bin/bash

# Remove the export variable if it already exists, set the new variable, then export it.
unset NAMESPACE
NAMESPACE=$1 ## This is the namespace where the Boutique Shop will be deployed.
PWD=$2 ## This is the working directory which holds the various yaml deployment files.
HOSTNAME=$3 ## This is the holder for the URL Prefix applied from the tenant manager
TIER=$4 ## Tier selection
export NAMESPACE
export PWD
#
# Check if the namespace already exists
#if oc get namespace -o json | jq -r ".items[].metadata.name" | grep $NAMESPACE; then \
#  echo "The Namespace $NAMESPACE already exists"
#else
#
# Create the Namespace
#echo "apiVersion: v1
#kind: Namespace
#metadata:
#  name: ${NAMESPACE}" | oc apply -f -
#fi
oc create ns $NAMESPACE
#
# Modify privileges for the defaut service account in scc. This step needs to be reviewed as the gives the service account too much privileges.
oc adm policy add-scc-to-user privileged -z default -n $NAMESPACE
#
# Change into the new Namespace
oc project ${NAMESPACE}
#

CLUSTER_DOMAIN=$(oc get ingresses.config.openshift.io cluster -ojsonpath='{.spec.domain}')
echo "CLUSTER_DOMAIN is $CLUSTER_DOMAIN"

# Deploy the all-in-one application stack
if [[ "$NAMESPACE" ]]; then
  oc expose svc frontend --name=$NAMESPACE --hostname=${HOSTNAME}.${CLUSTER_DOMAIN}
else
  oc apply -f ${PWD}/${TIER}/all-in-one.yaml
  #
  # **Need to create logic to monitor the website until the service is up and running**
  #
  # Expose the frontend service
  oc expose svc frontend --name=$NAMESPACE --hostname=${HOSTNAME}.${CLUSTER_DOMAIN}
fi
#
# Sleep statement to allow for the frontend service to come online
sleep 6
#
# Get the url for the website
ROUTE=`oc get route | cut -d" " -f4`
#for i in `curl -kvv $ROUTE`; do grep "HTTP\/1.1 200"
#
# Apply Horizontal Pod Autoscaler for the microservices
oc apply -f ${PWD}/${TIER}/hpa.yaml
#
# Apply a quota to the namespace
oc apply -f ${PWD}/${TIER}/boutique-quota.yaml
#
# Apply a limit in the namespace
oc apply -f ${PWD}/${TIER}/limit-range-v1.yaml
#
# Validation of the route
echo "The shop url is "http://${ROUTE}""

