FROM java:8
VOLUME /tmp
ADD agent-0.1.0-SNAPSHOT.jar wifimon-agent.jar
EXPOSE 8443
RUN bash -c 'touch /wifimon-agent.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/wifimon-agent.jar"]