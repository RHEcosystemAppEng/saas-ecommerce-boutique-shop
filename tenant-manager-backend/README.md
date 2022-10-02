# Red Hat SaaS Tenant Manager Backend

<img style="float: right;" src="./doc/img/Logo-Red_Hat-A-Standard-RGB.svg" title="Red Hat" width="400" align="right">

Red Hat SaaS (Software as a Service) reference implementation is a demonstration project to showcase how easy to 
implement a SaaS solution leveraging the Red Hat product portfolio. 


This project contains the basic user interfaces where users can request for an online ecommerce web-shop on demand 
with customizable resource requirements. This project is intend as a reference implementation example for any customer 
who is willing to provide their software solutions to their customer in a tenant based subscriptions.

### Overview

<img style="float: right;" src="./doc/img/overview.png">

### Installation Steps

. In the tenant-manager-backend, execte the following
  - mvn clean install -DskipTests
  - build the dcker image
  - push the docker image
  - change to the tenant-manager-frontend folder
  - modify the .env file for the correct path of the cluster that this is running on
  - do the docker build
  - do the docker push
  - deploy the ui with the following:
 
  ```
oc apply -f k8s/deploy.yaml
```
If the frontend is not running, you can run it locally with the following command: 
- npm install && npm run start
