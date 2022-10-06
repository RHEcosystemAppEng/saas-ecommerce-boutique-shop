FROM openjdk:8-jdk-alpine

WORKDIR /usr/app

COPY ./target/saas-tenant-manager-1.0.0-spring-boot.jar .

USER root

RUN microdnf install -y wget gzip

RUN mkdir /tmp/ocp && cd /tmp/ocp && wget https://mirror.openshift.com/pub/openshift-v4/x86_64/clients/ocp/stable/openshift-client-linux.tar.gz && \
      tar -zxvf openshift-client-linux.tar.gz && \
      mv oc /usr/bin/oc && mv kubectl /usr/bin/kubectl

RUN mkdir boutique_files

COPY ./src/main/resources/boutique_files/ boutique_files

RUN chmod +x boutique_files/*.sh

USER 1000

CMD ["java","-jar","saas-tenant-manager-1.0.0-spring-boot.jar"]

EXPOSE 8081
