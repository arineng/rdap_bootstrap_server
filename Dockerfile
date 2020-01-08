FROM jboss/wildfly:18.0.1.Final 

LABEL maintainer="gdubin@arin.net"

ENV version "1000.0-SNAPSHOT"

# COPY path-to-your-application-war path-to-webapps-in-docker-tomcat
COPY build/libs/rdap_bootstrap_server-${version}.war /opt/jboss/wildfly/standalone/deployments/
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
