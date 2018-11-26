FROM qaware/zulu-centos-payara-micro:8u192-5.183

COPY build/libs/graphql-nosql-javaee8.war /opt/payara/deployments/
