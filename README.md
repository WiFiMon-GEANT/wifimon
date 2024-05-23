# WiFiMon

## Build

    mvn clean install

## Official WiFiMon Documentation

The official WiFiMon documentation is available from: https://wiki.geant.org/display/WIF/WiFiMon+User+Documentation

##RabbitMQ service - PUB/SUB

Measurements from probes or TWAMP can be posted directly to the WAS server. Alternatively, these measurements can be published to RabbitMQ queues, with the application consuming the messages from these queues. This branch implements the latter option.

To set up and use RabbitMQ, follow these steps:

    - In twping_parser.py:

    Set the USE_STREAM_DATA variable to False.

    Additionally, in the use_publisher function:
    Insert the details for your configuration, such as the URL used in AMQP, the exchange name, and the routing key used for the queue.

    - In wireless.py:

    Follow the same steps as above.

Note: USE_STREAM_DATA variable is already initialized to False. By default, the measurements will be published to a RabbitMQ queue, when you use this branch.

Below are the changes that should be made in the secure-processor.properties file for RabbitMQ configuration:

    # name of the MQ used to fetch probes measurements
    rabbitmq.probesQueue.name=probesQueueTest
    # name of the key for the probes queue
    rabbitmq.probesRoutingKey.name=probesRoutingTest
    # name of the MQ used to fetch twamp measurements
    rabbitmq.twampQueue.name=twampQueueTest
    # name of the key for the twamp queue
    rabbitmq.twampRoutingKey.name=twampRoutingTest
    # name of the MQ exchange used to locate queues. Depending on your setup this may not be necessary.
    rabbitmq.exchange.name=test_exchange
    # URL of the rabbitMQ service hosting the queues. Below is a sample as used in CloudAMQP online service, your case may vary depending on the implementation used.
    cloudamqp_url=amqps://<user>:<password>@<host>/<user>

In our example, CloudAMQP is used, but any RabbitMQ service hosting queues can be implemented.