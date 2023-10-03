#!/bin/sh

java -jar /usr/lib/wifimon/ui-2.2.0.war --spring.config.location=/usr/lib/wifimon/config/ui.properties,file:/usr/lib/wifimon/config/ui.properties &
java -jar /usr/lib/wifimon/secure-processor-2.2.0.war --spring.config.location=/usr/lib/wifimon/config/secure-processor.properties,file:/usr/lib/wifimon/config/secure-processor.properties &
