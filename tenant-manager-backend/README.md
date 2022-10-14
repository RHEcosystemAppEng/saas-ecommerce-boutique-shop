# Tenant Manager Backend Reactive-Quarkus 🚀🚀🚀

This project is responsible for managing the Http API calls made by the ReactJS project (git branch `reactive`).

## How to Run

### Prerequisites
- Install oc-cli
- Connect to a Red Hat Openshift cluster and run `oc login` command
- Up and running Docker environment
- Java 17
- Maven 3.8+

1. Execute `mvn clean package`
2. Build the docker image using `docker build -t quay.io/<<username>>/tenant-manager-backend:latest -f ./src/main/docker/Dockerfile.native .` command
3. Push the docker image to the repository using `docker push -t quay.io/<<username>>/tenant-manager-backend:latest` command
4. Change the container image paths inside the ../k8s/deploy.yaml file
5. Run `oc apply -f ../k8s/deploy.yaml`


Navigate to the project directory and run the following command:
```
quarkus dev
```