#!/bin/bash

# usage: post-init.sh OPERATOR_DIR OPERATOR_NAME NAMESPACE

OPERATOR_DIR=$1
OPERATOR_NAME=$2
NAMESPACE=$3

cat << EOF > ${OPERATOR_DIR}/config/rbac/additional_role_binding.yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: boutique-shop-deployer-rolebinding
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: boutique-shop-deployer-role
subjects:
- kind: ServiceAccount
  name: ${OPERATOR_NAME}-controller-manager
  namespace: ${NAMESPACE}
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: boutique-shop-deployer-role
rules:
- apiGroups:
  - config.openshift.io
  resources:
  - ingresses
  verbs:
  - get
EOF

cat << EOF >> ${OPERATOR_DIR}/config/rbac/kustomization.yaml
# Add service account
- additional_role_binding.yaml
EOF