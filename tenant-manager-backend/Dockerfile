FROM registry.access.redhat.com/ubi8/openjdk-11-runtime:1.10-1

WORKDIR /usr/app

COPY ./target/saas-tenant-manager-1.0.0-spring-boot.jar .

#RUN mkdir boutique_files
#
#COPY ./src/main/resources/boutique_files/* boutique_files

CMD ["java","-jar","saas-tenant-manager-1.0.0-spring-boot.jar"]

EXPOSE 8080