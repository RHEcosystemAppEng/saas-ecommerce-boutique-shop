#!/usr/bin/env bash

IMAGE_REGISTRY=quay.io
IMAGE_REPOSITORY=ecosystem-appeng
IMAGE_NAME=saas-tenant-manager-frontend

#BUILD_VERSION=$(grep '"version"' package.json | cut -d ':' -f2 | tr -d ' ",')
BUILD_VERSION=0.0.1
echo Build version is: ${BUILD_VERSION}
IMAGE_FULL=$IMAGE_REGISTRY/$IMAGE_REPOSITORY/$IMAGE_NAME:$BUILD_VERSION
IMAGE_LATEST=${IMAGE_REGISTRY}/${IMAGE_REPOSITORY}/${IMAGE_NAME}:latest
echo Building image: ${IMAGE_FULL}
#docker login "${{ env.IMAGE_REGISTRY }}" --username="${{ secrets.REGISTRY_USERNAME }}" --password="${{ secrets.REGISTRY_PASSWORD }}"

docker build --build-arg BUILD_VERSION=${BUILD_VERSION} -t ${IMAGE_FULL} .
docker tag ${IMAGE_FULL} ${IMAGE_LATEST}
docker push ${IMAGE_FULL}
docker push ${IMAGE_LATEST}
