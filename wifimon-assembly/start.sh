#!/bin/sh

java -jar /usr/lib/wifimon/ui-0.1.1-SNAPSHOT.war --spring.config.location=classpath:/usr/lib/wifimon/config/ui.properties,file:/usr/lib/wifimon/config/ui.properties &
java -jar /usr/lib/wifimon/secure-processor-0.1.1-SNAPSHOT.war --spring.config.location=classpath:/usr/lib/wifimon/config/secure-processor.properties,file:/usr/lib/wifimon/config/secure-processor.properties &


