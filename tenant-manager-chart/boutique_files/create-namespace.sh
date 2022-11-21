#!/bin/bash

TENANT_NAME=$(echo "$1" | tr '[:upper:]' '[:lower:]')  ## This can be the namespace where the Boutique Shop will be deployed, and the name of the associated Route
TENANT_HOSTNAME=$(echo "$2" | tr '[:upper:]' '[:lower:]')  ## This is the host name for the tenant access Route
TIER=$(echo "$3" | tr '[:upper:]' '[:lower:]') ## Tier selection - Ensure it's all lowercase

SCRIPT_DIR=$(dirname $0)
TENANT_NAMESPACE=${TENANT_NAME}

## Constants
FREE_NAMESPACE=boutique-free
SILVER_NAMESPACE=boutique-silver
GOLD_NAMESPACE=$TENANT_NAME

ERR_NS_ALREADY_EXISTS=1
ERR_UNMANAGED_TIER=2

function log() {
  echo $1
}

## Returns 0 if the NS is missing, 1 otherwise
function checkNamespaceExists() {
  namespaceInCluster=$(oc get namespace $1 -oname --ignore-not-found=true)
  if [ ! -z "$namespaceInCluster" ]; then
    log "The Namespace $1 already exists"
    return 1
  else
    return 0
  fi
}

## Returns 0 if the NS was missing, 1 otherwise
function createNamespaceIfMissingAndSetProject() {
  checkNamespaceExists $1
  if [ $? -eq 0 ]; then
    log "Creating namespace $1"
    oc create namespace $1
  fi
  oc project $1
}


function createGoldNamespaceIfMisingAndSetProject() {
 
  base1=enterprise-utilities
  base2=boutique-ops

  createNamespaceIfMissingAndSetProject $base1
  createNamespaceIfMissingAndSetProject $base2

  log "Creating tenant namespace $1 for the Gold tier."
  createNamespaceIfMissingAndSetProject $1

  oc adm policy add-scc-to-user privileged -z default -n $base1
  oc adm policy add-scc-to-user privileged -z default -n $base2

}


function createNewNamespaceAndSetProject() {
  checkNamespaceExists $1
  if [ $? -eq 1 ]; then
    log "Namespace $1 aready exists. Exiting with error code ${ERR_NS_ALREADY_EXISTS}"
    exit ${ERR_NS_ALREADY_EXISTS}
  fi
  oc create namespace $1
  oc project $1
}


function provisionAllGoldResources() {

  tierFolder=${SCRIPT_DIR}/${TIER}

  # Modify privileges for the defaut service account in scc. This step needs to be reviewed as the gives the service account too much privileges.
  oc adm policy add-scc-to-user privileged -z default -n $1

  # Deploy the all-in-one application stack
  oc process -f ${tierFolder}/all-in-one.yaml --param=TENANT_NAMESPACE=$1 | oc apply -f -
  # Apply Horizontal Pod Autoscaler for the microservices
  oc apply -f ${tierFolder}/hpa.yaml
  # Apply a quota to the namespace
  oc apply -f ${tierFolder}/boutique-quota.yaml
  # Apply a limit in the namespace
  oc apply -f ${tierFolder}/limit-range-v1.yaml

}

function provisionAllResources() {

  tierFolder=${SCRIPT_DIR}/${TIER}

  # Modify privileges for the defaut service account in scc. This step needs to be reviewed as the gives the service account too much privileges.
  oc adm policy add-scc-to-user privileged -z default -n $1

  # Deploy the all-in-one application stack
  oc apply -f ${tierFolder}/all-in-one.yaml
  # Apply Horizontal Pod Autoscaler for the microservices
  oc apply -f ${tierFolder}/hpa.yaml
  # Apply a quota to the namespace
  oc apply -f ${tierFolder}/boutique-quota.yaml
  # Apply a limit in the namespace
  oc apply -f ${tierFolder}/limit-range-v1.yaml
}

function createRouteAndExportURL(){

  clusterDomain=$(oc get ingresses.config.openshift.io cluster -ojsonpath='{.spec.domain}')
  log "Cluster domain is ${clusterDomain}"
  # **Need to create logic to monitor the website until the service is up and running**
  oc expose svc frontend --name=${TENANT_NAME} --hostname=${TENANT_HOSTNAME}.${clusterDomain}
  # Sleep statement to allow for the frontend service to come online
  sleep 6

  #
  # Get the url for the website
  ROUTE=$(oc get route ${TENANT_NAME} --no-headers | awk '{print $4"://"$2}')

  #
  # Validation of the route
  echo "${ROUTE}"
}

function provisionFree() {
  log "Provisioining free tier"
  createNamespaceIfMissingAndSetProject ${FREE_NAMESPACE}
  if [ $? -eq 0 ]; then
    provisionAllResources ${FREE_NAMESPACE}
  fi
  createRouteAndExportURL ${FREE_NAMESPACE}
}

function provisionSilver() {
  log "Provisioining silver tier"
  createNamespaceIfMissingAndSetProject ${SILVER_NAMESPACE}
  if [ $? -eq 0 ]; then
    provisionAllResources ${SILVER_NAMESPACE}
  fi
  createRouteAndExportURL ${SILVER_NAMESPACE}
}

function provisionGold() {
  log "Provisioining gold tier"
  createGoldNamespaceIfMisingAndSetProject ${GOLD_NAMESPACE}
  if [ $? -eq 0 ]; then
    provisionAllGoldResources ${GOLD_NAMESPACE}
  fi
  createRouteAndExportURL ${GOLD_NAMESPACE}
}

function provisionPremium() {
  createNewNamespaceAndSetProject ${TENANT_NAMESPACE}
  provisionAllResources ${TENANT_NAMESPACE}
  createRouteAndExportURL ${TENANT_NAMESPACE}
}

echo "Provisioning for tenant ${TENANT_NAME}, host ${TENANT_HOSTNAME} and tier ${TIER}"
if [[ ${TIER} == "free" ]]; then
  provisionFree
elif [[ ${TIER} == "silver" ]]; then
  provisionSilver
elif [[ ${TIER} == "gold" ]]; then
  provisionGold
elif [[ ${TIER} == "premium" ]]; then
  provisionPremium
else
  echo "Unmanaged tier ${TIER}-Exiting with error code ${ERR_UNMANAGED_TIER}"
  exit ${ERR_UNMANAGED_TIER}
fi