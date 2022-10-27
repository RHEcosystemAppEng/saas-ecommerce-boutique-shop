# Build the manager binary
FROM registry.redhat.io/openshift4/ose-helm-operator:v4.11

ENV HOME=/opt/helm
COPY watches.yaml ${HOME}/watches.yaml
COPY helm-charts  ${HOME}/helm-charts
WORKDIR ${HOME}
