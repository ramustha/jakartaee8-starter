# Base image: Wildfly with 8080 port exposed
FROM jboss/wildfly

# Open management port
EXPOSE 8080
EXPOSE 9990
EXPOSE 8787

# Add management user with password
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent

# Set the default command to run on boot
# This will boot WildFly in the standalone mode and bind to all interface
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
