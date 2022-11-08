#!/bin/bash

# usage: post-init.sh OPERATOR_DIR OPERATOR_NAME

OPERATOR_DIR=$1
OPERATOR_NAME=$2

mkdir -p  ${OPERATOR_DIR}/config/manifests/bases
cat << EOF > ${OPERATOR_DIR}/config/manifests/bases/${OPERATOR_NAME}.clusterserviceversion.yaml
apiVersion: operators.coreos.com/v1alpha1
kind: ClusterServiceVersion
metadata:
  annotations:
    alm-examples: '[]'
    capabilities: Basic Install
  name: ${OPERATOR_NAME}.v0.0.0
  namespace: placeholder
spec:
  apiservicedefinitions: {}
  customresourcedefinitions: {}
  description: Deploys Boutique Tenant Manager operator to provision multiple tenants
    in SaaS environment
  displayName: Boutique Tenant Manager
  icon:
  - base64data: ""
    mediatype: ""
  install:
    spec:
      deployments: null
    strategy: ""
  installModes:
  - supported: false
    type: OwnNamespace
  - supported: false
    type: SingleNamespace
  - supported: false
    type: MultiNamespace
  - supported: true
    type: AllNamespaces
  keywords:
  - boutique
  - saas
  - tenant
  links:
  - name: Boutique Tenant Operator
    url: https://boutique-tenant-operator.domain
  maturity: alpha
  provider:
    name: RedHat Inc
    url: https://www.redhat.com
  version: 0.0.0
EOF