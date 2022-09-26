#!/bin/bash

# Remove the export variable if it already exists, set the new variable, then export it.
unset NAMESPACE
NAMESPACE=boutique
PWD=$2
export NAMESPACE
export PWD
#
# Check if the namespace already exists
#if oc get namespace -o json | jq -r ".items[].metadata.name" | grep -w $NAMESPACE; then \
#  echo "The Namespace $NAMESPACE already exists"
#else
  echo "Starting to provision OCP resources..."
  # Create the Namespace
#  echo "apiVersion: v1 \
#  kind: Namespace \
#  metadata: \
#    name: ${NAMESPACE}" | oc create -f -

  oc create ns $NAMESPACE


  # Modify privileges for the default service account in scc. This step needs to be reviewed as the gives the service account too much privileges.
  oc adm policy add-scc-to-user privileged -z default -n $1
  #
  # Change into the new Namespace
  oc project ${NAMESPACE}
  #
  # Deploy the all-in-one application stack
  oc apply -f ${PWD}/all-in-one.yaml
  #
  # **Need to create logic to monitor the website until the service is up and running**
  #
  # Expose the frontend service
  oc expose svc frontend --name=$1-route
  #--hostname=$2.pebcac.org
  #
  # Get the url for the website
  ROUTE=`oc get route | cut -d" " -f4 | xargs`
  #for i in `curl -kvv $ROUTE`; do grep "HTTP\/1.1 200"
  #
  # Apply a quota to the namespace
  oc apply -f ${PWD}/boutique-quota.yaml
  #
  # Apply autoscaling for the frontend service
  oc apply -f ${PWD}/frontend-hpa.yaml

  echo "The shop url is "http://${ROUTE}""

#fi
