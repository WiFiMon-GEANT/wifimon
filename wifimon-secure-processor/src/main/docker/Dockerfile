FROM java:8
VOLUME /tmp
ADD secure-processor-0.1.1-SNAPSHOT.jar wifimon-secure-procesor.jar
EXPOSE 8443
RUN bash -c 'touch /wifimon-secure-procesor.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/wifimon-secure-procesor.jar"]