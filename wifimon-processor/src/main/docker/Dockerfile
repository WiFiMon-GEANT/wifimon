FROM java:8
VOLUME /tmp
ADD processor-0.1.0.jar wifimon-procesor.jar
EXPOSE 9000
RUN bash -c 'touch /wifimon-procesor.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/wifimon-procesor.jar"]