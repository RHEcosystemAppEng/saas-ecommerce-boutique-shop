#!/bin/bash

# Remove the export variable if it already exists, set the new variable, then export it.
unset NAMESPACE
NAMESPACE=$1
PWD=$2
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
# Deploy the all-in-one application stack
oc apply -f ${PWD}/all-in-one.yaml
#
# **Need to create logic to monitor the website until the service is up and running**
#
# Expose the frontend service
oc expose svc frontend --name=$NAMESPACE --hostname=$3.apps.ocp.pebcac.org
#
# Sleep statement to allow for the frontend service to come online
sleep 6
#
# Get the url for the website
ROUTE=`oc get route | cut -d" " -f4`
#for i in `curl -kvv $ROUTE`; do grep "HTTP\/1.1 200"
#
# Apply a quota to the namespace
oc apply -f ${PWD}/boutique-quota.yaml
#
# Apply a limit in the namespace
oc apply -f ${PWD}/limit-range-v1.yaml
#
# Validation of the route
echo "The shop url is "http://${ROUTE}""

---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: adservice-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: adservice

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: adservice-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: adservice
    
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: cartservice-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: cartservice

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: checkout-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: checkoutservice

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: currencyservice-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: currencyservice

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: emailservice-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: emailservice
kind: HorizontalPodAutoscaler
apiVersion: autoscaling/v2
metadata:
  name: frontend-autoscaler
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: frontend
  minReplicas: 1
  maxReplicas: 3
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: paymentservice-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: paymentservice

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: productcatalog-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: productcatalogservice

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: recommend-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: recommendationservice
    
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: redis-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: redis-cart

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: shipping-autoscaler
spec:
  maxReplicas: 3
  metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70
        type: Utilization
    type: Resource
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: shippingservice
