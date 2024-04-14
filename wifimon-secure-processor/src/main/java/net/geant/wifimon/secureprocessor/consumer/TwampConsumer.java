package net.geant.wifimon.secureprocessor.consumer;

import net.geant.wifimon.model.dto.TwampMeasurement;
import net.geant.wifimon.secureprocessor.resource.AggregatorResource;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import jakarta.ws.rs.core.Response;

@Service
public class TwampConsumer extends AggregatorResource {

    @RabbitListener(queues = {"${rabbitmq.twampQueue.name}"})
    public Response twampConsume(TwampMeasurement twampMeasurement) {
        return handleTwampMeasurements(twampMeasurement);
    }
}
