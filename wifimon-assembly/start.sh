#!/bin/sh

#java -jar processor-0.1.1-SNAPSHOT.jar --spring.config.location=classpath:/processor.properties,file:./config/processor.properties &
java -jar secure-processor-0.1.1-SNAPSHOT.jar --spring.config.location=classpath:/secure-processor.properties,file:./config/secure-processor.properties &
java -jar ui-0.1.1-SNAPSHOT.jar --spring.config.location=classpath:/ui.properties,file:./config/ui.properties &