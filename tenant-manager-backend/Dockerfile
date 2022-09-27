FROM registry.access.redhat.com/ubi8/openjdk-11-runtime:1.10-1

WORKDIR /usr/app

COPY ./target/saas-tenant-manager-1.0.0-spring-boot.jar .

RUN mkdir /tmp/ocp && cd /tmp/ocp && wget https://mirror.openshift.com/pub/openshift-v4/x86_64/clients/ocp/stable/openshift-client-linux.tar.gz && \
      tar -zxvf openshift-client-linux.tar.gz && \
      sudo mv oc /usr/bin/oc && sudo mv kubectl /usr/bin/kubectl


RUN mkdir boutique_files

COPY ./src/main/resources/boutique_files/* boutique_files

USER root

RUN chmod +x boutique_files/*.sh

USER 1001

CMD ["java","-jar","saas-tenant-manager-1.0.0-spring-boot.jar"]

EXPOSE 8081
