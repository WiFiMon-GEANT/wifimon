#!/bin/sh

java -jar /usr/lib/wifimon/ui-1.1.1-SNAPSHOT.war --spring.config.location=classpath:/usr/lib/wifimon/config/ui.properties,file:/usr/lib/wifimon/config/ui.properties &
java -jar /usr/lib/wifimon/secure-processor-1.1.1-SNAPSHOT.war --spring.config.location=classpath:/usr/lib/wifimon/config/secure-processor.properties,file:/usr/lib/wifimon/config/secure-processor.properties &


